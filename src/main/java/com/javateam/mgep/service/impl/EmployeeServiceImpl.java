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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
        if(findEmployee.isPresent()) {
            throw new EmailAlreadyUsedException();

        }
        if(!employeeData.getPassword().equals(employeeData.getRepeatPassword())) {
            throw new PasswordNotMatchException();
        }
        Employee newEmployee = new Employee();
        newEmployee.setFirstName(employeeData.getFirstName());
        newEmployee.setLastName(employeeData.getLastName());
        Date date1 =new SimpleDateFormat("yyyy-MM-dd").parse(employeeData.getDateOfBirth());
        newEmployee.setDateOfBirth(date1);
        newEmployee.setPhoneNumber(employeeData.getPhoneNumber());
        newEmployee.setEmail(employeeData.getEmail().toLowerCase());
        Optional<Department> findDept = departmentRepository.findById(employeeData.getDeptId());
        if(findDept.isPresent()) {
            newEmployee.setDepartment(findDept.get());
        }
        newEmployee.setPasswordHash(passwordEncoder.encode(employeeData.getPassword()));
        newEmployee.setGender(employeeData.getGender());
        newEmployee.setAddress(employeeData.getAddress()); //Bổ dung thêm địa chỉ khi user đăng kí tài khoản.
        newEmployee.setStatus("0");
        Set<Authoritty> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newEmployee.setAuthorities(authorities);
        newEmployee.setCreateDate(new Date());
        Employee employeeSaved = employeeRepository.save(newEmployee);
        if(employeeSaved!=null) {
            ConfirmationToken confirmationToken = new ConfirmationToken(employeeSaved);
            confirmationTokenRepository.save(confirmationToken);
            mailService.sendActiveMail(employeeSaved, confirmationToken.getConfirmationToken());
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
    public List<Employee> getListAll() {
        return employeeRepository.findAll();
    }

    @Override
    public List<Employee> searchByData(SearchCriteria searchCriteria) {
        return employeeRepository.findEmployeeByFields(searchCriteria.getDataSearch());
    }
}
