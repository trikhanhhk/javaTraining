package com.javateam.mgep.service.impl;

import com.javateam.mgep.constants.AuthoritiesConstants;
import com.javateam.mgep.entity.Authoritty;
import com.javateam.mgep.entity.ConfirmationToken;
import com.javateam.mgep.entity.Department;
import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.entity.dto.EmployeeData;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
    public Employee addEmployee(EmployeeData employeeData) {
        Optional<Employee> findEmployee = employeeRepository.findOneByEmailIgnoreCase(employeeData.getEmail());
        if(findEmployee.isPresent()) {
            throw new EmailAlreadyUsedException();

        }
        if(!employeeData.getPassword().equals(employeeData.getRepeatPassword())) {
            throw new PasswordNotMatchException();
        }
        DateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        Date dateOfBirth = null;
        try {
            dateOfBirth = date.parse(employeeData.getDateOfBirth());
        }catch (Exception e){
            e.printStackTrace();
        }
        Employee newEmployee = new Employee();
        newEmployee.setFirstName(employeeData.getFirstName());
        newEmployee.setLastName(employeeData.getLastName());
        newEmployee.setDateOfBirth(dateOfBirth);
        newEmployee.setPhoneNumber(employeeData.getPhoneNumber());
        newEmployee.setEmail(employeeData.getEmail().toLowerCase());
        Optional<Department> findDept = departmentRepository.findById(employeeData.getDeptId());
        if(findDept.isPresent()) {
            newEmployee.setDepartment(findDept.get());
        }
        newEmployee.setPasswordHash(passwordEncoder.encode(employeeData.getPassword()));
        newEmployee.setGender(employeeData.getGender());
        newEmployee.setStatus(0);
        newEmployee.setAddress(employeeData.getAddress());
        Set<Authoritty> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newEmployee.setAuthorities(authorities);
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
}
