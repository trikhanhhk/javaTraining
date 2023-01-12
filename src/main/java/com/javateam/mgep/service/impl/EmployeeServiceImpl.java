package com.javateam.mgep.service.impl;

import com.javateam.mgep.constants.AuthoritiesConstants;
import com.javateam.mgep.entity.Authoritty;
import com.javateam.mgep.entity.ConfirmationToken;
import com.javateam.mgep.entity.Department;
import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.entity.dto.EmployeeData;
import com.javateam.mgep.entity.dto.SearchCriteria;
import com.javateam.mgep.exception.EmailAlreadyUsedException;
import com.javateam.mgep.exception.PasswordNotMatchException;
import com.javateam.mgep.repositories.AuthorityRepository;
import com.javateam.mgep.repositories.ConfirmationTokenRepository;
import com.javateam.mgep.repositories.DepartmentRepository;
import com.javateam.mgep.repositories.EmployeeRepository;
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
    @Autowired DepartmentRepository departmentRepository;
    @Autowired
    AuthorityRepository authorityRepository;
    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    MailService mailService;

    private final PasswordEncoder passwordEncoder;

    public EmployeeServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Employee addEmployee(EmployeeData employeeData) throws ParseException {
        Optional<Employee> findEmployee = employeeRepository.findOneByEmailIgnoreCase(employeeData.getEmail());
        if(findEmployee.isPresent()) { //check mail đã tồn tại hay chưa
            throw new EmailAlreadyUsedException();

        }
        if(!employeeData.getPassword().equals(employeeData.getRepeatPassword())) {
            throw new PasswordNotMatchException();
        }
        //lưu thông tin nhân viên
        Employee newEmployee = new Employee();
        newEmployee.setFirstName(employeeData.getFirstName());
        newEmployee.setLastName(employeeData.getLastName());
        Date date1 =new SimpleDateFormat("yyyy-MM-dd").parse(employeeData.getDateOfBirth());  //định dạng ngày tháng
        newEmployee.setDateOfBirth(date1);
        newEmployee.setPhoneNumber(employeeData.getPhoneNumber());
        newEmployee.setEmail(employeeData.getEmail().toLowerCase());
        Optional<Department> findDept = departmentRepository.findById(employeeData.getDeptId()); //lấy phòng ban theo mã đã chọn
        if(findDept.isPresent()) {
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
        if(employeeSaved!=null) {  //nếu lưu thành công
            ConfirmationToken confirmationToken = new ConfirmationToken(employeeSaved); // tạo ra token mới để xác thực tài khoản
            confirmationTokenRepository.save(confirmationToken);  // lưu token vào db
            mailService.sendActiveMail(employeeSaved, confirmationToken.getConfirmationToken()); //gửi mail cho tài khoản
            return newEmployee;
        }
        return null;
    }

    @Override
    public Employee updateEmployee(String address, String phone,String email) {
        Employee employee = employeeRepository.findByEmail(email);
        if (employee != null){
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
    public Employee updateEmployeeAdmin(EmployeeData employeeData) {
        Employee employee = employeeRepository.findByEmail(employeeData.getEmail());
        if (employee == null){
            return null;
        }
        employee.setFirstName(employeeData.getFirstName());
        employee.setLastName(employeeData.getLastName());
        employee.setGender(employeeData.getGender());
        employee.setDepartment(departmentRepository.findById(employeeData.getDeptId()).get());
        employee.setUpdateDate(new Date());
        Date date1;
        try {
            date1 =new SimpleDateFormat("yyyy-MM-dd").parse(employeeData.getDateOfBirth());
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
    public List<Employee> getListAll() {
        return employeeRepository.findAll();
    }

    @Override
    public List<Employee> searchByData(SearchCriteria searchCriteria) {
        if(searchCriteria.getDataSearch() == null || searchCriteria.getDataSearch().equals("")) {
            return employeeRepository.findAll();
        }
        return employeeRepository.findAllByEmail(searchCriteria.getDataSearch());
    }

    @Override
    public List<Employee> importFileEx(MultipartFile file) throws Exception{
        List<Employee> lstEmployee = new ArrayList<>();
        XSSFWorkbook workbook;
        workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheet("NhanVien");
        for (int index = 0; index < worksheet.getPhysicalNumberOfRows(); index++) {
            if (index > 0){
                EmployeeData employee = new EmployeeData();
                XSSFRow row = worksheet.getRow(index);
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
                    lstEmployee.add(addEmployee(employee));
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

//    public List<Employee> findByDeptId(String deptId) {
//        List<Employee> employeeList = employeeRepository.findAll();
//
//    }
}
