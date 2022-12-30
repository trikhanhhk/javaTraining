package com.javateam.mgep.service.impl;

import com.javateam.mgep.entity.CustomUserDetails;
import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.repositories.EmployeeRepository;
import com.javateam.mgep.service.ChangePasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ChangePasswordServiceIpml implements ChangePasswordService {

    @Autowired
    PasswordEncoder encoder;
    @Autowired
    EmployeeRepository employeeRepository;


    public ChangePasswordServiceIpml(PasswordEncoder encoder, EmployeeRepository employeeRepository) {
        this.encoder = encoder;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Employee changePassword(String oldPassword, String beforeNewPassword, String afterNewPassword) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        CustomUserDetails userDetails = (CustomUserDetails) securityContext.getAuthentication().getPrincipal();
        if (!encoder.matches(oldPassword,userDetails.getEmployee().getPasswordHash())){
            return null;
        }
        if (!beforeNewPassword.equals(afterNewPassword)){
            return null;
        }
        Employee employee = employeeRepository.findByEmail(userDetails.getUsername());
        String passwordNew = encoder.encode(beforeNewPassword);
        employee.setPasswordHash(passwordNew);
        employeeRepository.save(employee);
        return employee;
    }
}
