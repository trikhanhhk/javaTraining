package com.javateam.mgep.service.impl;

import com.javateam.mgep.constants.AuthoritiesConstants;
import com.javateam.mgep.constants.CommonConstants;
import com.javateam.mgep.entity.*;
import com.javateam.mgep.entity.dto.EmployeeData;
import com.javateam.mgep.entity.dto.SearchCriteria;
import com.javateam.mgep.exception.EmailAlreadyUsedException;
import com.javateam.mgep.repositories.*;
import com.javateam.mgep.service.EmailJob;
import com.javateam.mgep.service.EmployeeService;
import com.javateam.mgep.service.MailService;
import com.javateam.mgep.service.TimeJob;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    MailService mailService;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    AuthorityRepository authorityRepository;
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    ResetPasswordTokenRepository resetPasswordTokenRepository;

    @Autowired
    Scheduler scheduler;

    private final PasswordEncoder passwordEncoder;

    public EmployeeServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    //Add one employee
    public Employee addEmployee(EmployeeData employeeData, boolean importExFlg) throws ParseException, SchedulerException {
        Optional<Employee> findEmployee = employeeRepository.findOneByEmailIgnoreCase(employeeData.getEmail());
        if (findEmployee.isPresent()) { //check mail đã tồn tại hay chưa
            throw new EmailAlreadyUsedException();

        }
        //lưu thông tin nhân viên
        Employee newEmployee = new Employee();
        newEmployee.setFirstName(employeeData.getFirstName());
        newEmployee.setLastName(employeeData.getLastName());
        Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(employeeData.getDateOfBirth());  //định dạng ngày tháng
        newEmployee.setDateOfBirth(date1);
        newEmployee.setPhoneNumber(employeeData.getPhoneNumber());
        newEmployee.setEmail(employeeData.getEmail().toLowerCase());
        Optional<Department> findDept = departmentRepository.findById(employeeData.getDeptId()); //lấy phòng ban theo mã đã chọn
        if (findDept.isPresent()) {
            newEmployee.setDepartment(findDept.get()); //set giá trị phòng ban
        }
        String passRandom = this.getPasswordRandom(8);
        if (importExFlg) {
            newEmployee.setPasswordHash(passwordEncoder.encode(passRandom));
        } else {
            newEmployee.setPasswordHash(passwordEncoder.encode(employeeData.getPassword()));
        }
        newEmployee.setGender(employeeData.getGender());
        newEmployee.setAddress(employeeData.getAddress()); //Bổ dung thêm địa chỉ khi user đăng kí tài khoản.
        if (importExFlg) {
            newEmployee.setStatus("1");
        } else {
            newEmployee.setStatus("0");
        }
        Set<Authoritty> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newEmployee.setAuthorities(authorities);
        newEmployee.setCreateDate(new Date());
        Employee employeeSaved = employeeRepository.save(newEmployee); //lưu vào db
        if (employeeSaved != null) {  //nếu lưu thành công
            if(importExFlg) {
                mailService.sendPasswordNewAcount(employeeSaved, passRandom);
            } else {
                ConfirmationToken confirmationToken = new ConfirmationToken(employeeSaved); // tạo ra token mới để xác thực tài khoản
                confirmationTokenRepository.save(confirmationToken);  // lưu token vào db
                mailService.sendActiveMail(employeeSaved, confirmationToken.getConfirmationToken()); //gửi mail cho tài khoản
                scheduleTaskWithFixedRate(employeeSaved, new Date(employeeSaved.getCreateDate().getTime() + 30*24*1000*60*60)); // Sau 30 ngày nếu không xác nhận mail tài khoản sẽ bị xóa
            }
            return newEmployee;
        }
        return null;
    }

    // Tạo password ngẫu nhiên
    static String getPasswordRandom(int n)
    {

        // choose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    @Override
    // Update employee display screen user
    public Employee updateEmployee(String address, String phone, String email) {
        Employee employee = employeeRepository.findByEmail(email);
        if (employee != null) {
            employee.setAddress(address);
            employee.setPhoneNumber(phone);
            employeeRepository.save(employee);
            System.out.println("update thành công!");
            return employee;
        }
        return null;
    }

    @Override
    // Update user display screen admin
    public Employee updateEmployeeAdmin(EmployeeData employeeData, String role) {
        Employee employee = employeeRepository.findByEmail(employeeData.getEmail());
        if (role != null) {
            Authoritty authoritty = new Authoritty(role);
            Set<Authoritty> authoritySet = new HashSet<>();
            authoritySet.add(authoritty);
            employee.setAuthorities(authoritySet);
        }
        if (employee == null) {
            return null;
        }
        employee.setFirstName(employeeData.getFirstName());
        employee.setLastName(employeeData.getLastName());
        employee.setGender(employeeData.getGender());
        employee.setDepartment(departmentRepository.findById(employeeData.getDeptId()).get());
        employee.setUpdateDate(new Date());
        Date date1;
        try {
            date1 = new SimpleDateFormat("yyyy-MM-dd").parse(employeeData.getDateOfBirth());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        employee.setDateOfBirth(date1);
        employee.setAddress(employeeData.getAddress());
        employee.setPhoneNumber(employeeData.getPhoneNumber());

        employeeRepository.save(employee);
        return employee;
    }

    @Override
    //Find By All Employee
    public List<Employee> getListAll() {
        return employeeRepository.findAll();
    }

    @Override
    //Search information
    public List<Employee> searchByData(SearchCriteria searchCriteria) {
        if (searchCriteria.getDataSearch() == null || searchCriteria.getDataSearch().equals("")) {
            return employeeRepository.findAll();
        }
        return employeeRepository.findAllByEmail(searchCriteria.getDataSearch());
    }

    @Override
    //Function Import File Excel
    public List<Employee> importFileEx(MultipartFile file) throws Exception {
        List<Employee> lstEmployee = new ArrayList<>();
        List<EmployeeData> employeeDataList = new ArrayList<>();

        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        //Read file sheet name NhanVien
        XSSFSheet worksheet = workbook.getSheet("NhanVien");
        try {
            if (worksheet == null) {
                return null;
            } else {
                for (int index = 0; index < worksheet.getPhysicalNumberOfRows(); index++) {

                    if (index > 0) {
                        EmployeeData employeeData = new EmployeeData();
                        // Get One Row in file excel.
                        XSSFRow row = worksheet.getRow(index);
                        // Set information employee for row in file excel.
                        employeeData.setFirstName(row.getCell(1).getStringCellValue());
                        employeeData.setLastName(row.getCell(2).getStringCellValue());
                        employeeData.setDateOfBirth(row.getCell(3).getStringCellValue());
                        employeeData.setPhoneNumber(row.getCell(4).getStringCellValue());
                        String nameGender = (row.getCell(5).getStringCellValue());
                        employeeData.setGender("Nam".equals(nameGender) ? "1" : "0");
                        employeeData.setEmail(row.getCell(6).getStringCellValue());
                        employeeData.setAddress(row.getCell(7).getStringCellValue());
                        String nameDepartment = row.getCell(8).getStringCellValue();
                        Department department = departmentRepository.findByName(nameDepartment);
                        employeeData.setDeptId(department.getId());

                        //addEmployeeData => list Employee Data.
                        employeeDataList.add(employeeData);
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        //Save employeeData -> employee and call function addEmployee save database.
        for (EmployeeData employeeData : employeeDataList) {
            try {
                if (employeeRepository.findByEmail(employeeData.getEmail()) != null) {
                    this.updateEmployeeAdmin(employeeData, null);
                } else {
                    lstEmployee.add(addEmployee(employeeData, true));
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            workbook.close();
            return lstEmployee;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    //Delete employee by id
    public String deleteEmployeeById(Long id) {
        Optional<Employee> employeeFindById = employeeRepository.findById(id);
        if (employeeFindById.isEmpty()) {
            return null;
        }
        Employee employee = employeeFindById.get();
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByUserEntity(employee.getId());
        if (confirmationToken != null) {
            confirmationTokenRepository.deleteById(confirmationToken.getTokenid());
        }

        List<ResetPasswordToken> resetPasswordToken = resetPasswordTokenRepository.findByUserEntity(employee);

        for (int i = 0; i < resetPasswordToken.size(); i++) {
            resetPasswordTokenRepository.deleteById(resetPasswordToken.get(i).gettkenId());
        }
        employeeRepository.delete(employee);
        return "OK";
    }

    public void scheduleTaskWithFixedRate(Employee e, Date satrtAt) throws SchedulerException {
        JobDetail jobDetail = buildJobDetail(e);
        Trigger trigger = buildJobTrigger(jobDetail, satrtAt);
        scheduler.scheduleJob(jobDetail, trigger);
    }

    private JobDetail buildJobDetail(Employee e) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(CommonConstants.JOB_NAME, CommonConstants.JOB_ACCOUNT_IS_ACTIVE);
        jobDataMap.put(CommonConstants.ID_DATA_JOB, e.getId());  //Truyền ID job để gửi

        return JobBuilder.newJob(TimeJob.class)
                .withIdentity(UUID.randomUUID().toString(), "employee-jobs")
                .withDescription("Check account active")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail, Date startAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "employee-triggers")
                .withDescription("Check account active")
                .startAt(startAt)  //thời gian gửi
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }
}
