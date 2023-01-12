package com.javateam.mgep.service.impl;

import com.javateam.mgep.constants.AuthoritiesConstants;
import com.javateam.mgep.entity.*;
import com.javateam.mgep.entity.dto.EmployeeData;
import com.javateam.mgep.entity.dto.SearchCriteria;
import com.javateam.mgep.exception.EmailAlreadyUsedException;
import com.javateam.mgep.exception.PasswordNotMatchException;
import com.javateam.mgep.repositories.*;
import com.javateam.mgep.service.EmployeeService;
import com.javateam.mgep.service.MailService;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
    EmployeeRepository employeeRepository;
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    AuthorityRepository authorityRepository;
    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    ResetPasswordTokenRepository resetPasswordTokenRepository;

    @Autowired
    MailService mailService;

    private final PasswordEncoder passwordEncoder;

    public EmployeeServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    //Add one employee
    public Employee addEmployee(EmployeeData employeeData) throws ParseException {
        Optional<Employee> findEmployee = employeeRepository.findOneByEmailIgnoreCase(employeeData.getEmail());
        if (findEmployee.isPresent()) { //check mail đã tồn tại hay chưa
            throw new EmailAlreadyUsedException();

        }
        if (!employeeData.getPassword().equals(employeeData.getRepeatPassword())) {
            throw new PasswordNotMatchException();
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
        newEmployee.setPasswordHash(passwordEncoder.encode(employeeData.getPassword()));
        newEmployee.setGender(employeeData.getGender());
        newEmployee.setAddress(employeeData.getAddress()); //Bổ dung thêm địa chỉ khi user đăng kí tài khoản.
        newEmployee.setStatus("0");
        Set<Authoritty> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newEmployee.setAuthorities(authorities);
        newEmployee.setCreateDate(new Date());
        Employee employeeSaved = employeeRepository.save(newEmployee); //lưu vào db
        if (employeeSaved != null) {  //nếu lưu thành công
            ConfirmationToken confirmationToken = new ConfirmationToken(employeeSaved); // tạo ra token mới để xác thực tài khoản
            confirmationTokenRepository.save(confirmationToken);  // lưu token vào db
            mailService.sendActiveMail(employeeSaved, confirmationToken.getConfirmationToken()); //gửi mail cho tài khoản
            return newEmployee;
        }
        return null;
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
        System.out.println("update thất bại");
        return null;
    }

    @Override
    // Update user display screen admin
    public Employee updateEmployeeAdmin(EmployeeData employeeData, String role) {
        Employee employee = employeeRepository.findByEmail(employeeData.getEmail());

        Authoritty authoritty = new Authoritty(role);
        Set<Authoritty> authoritySet = new HashSet<>();
        authoritySet.add(authoritty);

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
        employee.setAuthorities(authoritySet);

        employeeRepository.save(employee);
        return employee;
    }

    @Override
    //Find By All Employee
    public List<Employee> getListAll() {
        return employeeRepository.findAll();
    }

    @Override
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
        XSSFWorkbook workbook;
        workbook = new XSSFWorkbook(file.getInputStream());
        //Read file sheet name NhanVien
        XSSFSheet worksheet = workbook.getSheet("NhanVien");
        for (int index = 0; index < worksheet.getPhysicalNumberOfRows(); index++) {
            if (index > 0) {
                EmployeeData employee = new EmployeeData();
                // Get One Row in file excel.
                XSSFRow row = worksheet.getRow(index);

                // Set information employee for row in file excel.
                employee.setFirstName(row.getCell(1).getStringCellValue());
                employee.setLastName(row.getCell(2).getStringCellValue());
                employee.setDateOfBirth(row.getCell(3).getStringCellValue());
                employee.setPhoneNumber(row.getCell(4).getStringCellValue());
                String nameGender = (row.getCell(5).getStringCellValue());
                employee.setGender(nameGender.equals("Nam") ? "1" : "0");
                employee.setEmail(row.getCell(6).getStringCellValue());
                employee.setAddress(row.getCell(7).getStringCellValue());
                employee.setPassword(row.getCell(8).getStringCellValue());
                employee.setRepeatPassword(row.getCell(9).getStringCellValue());
                String nameDepartment = row.getCell(10).getStringCellValue();
                Department department = departmentRepository.findByName(nameDepartment);
                employee.setDeptId(department.getId());
                try {
                    if(employeeRepository.findByEmail(employee.getEmail()) != null) {
                        this.updateEmployeeAdmin(employee,null);
                    } else {
                        lstEmployee.add(addEmployee(employee));
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
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


}
