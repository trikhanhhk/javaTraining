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

    @Override
    public Employee changePassword(String oldPassword, String beforeNewPassword, String afterNewPassword) {
        //Get information user to security;
        SecurityContext securityContext = SecurityContextHolder.getContext();
        CustomUserDetails userDetails = (CustomUserDetails) securityContext.getAuthentication().getPrincipal();

        Employee employee = employeeRepository.findByEmail(userDetails.getUsername());

        //Check password -> oldPassword with password user import.
        if (!encoder.matches(oldPassword,userDetails.getEmployee().getPasswordHash())){
            return null;
        }
        //Check new password is it the same?
        if (!beforeNewPassword.equals(afterNewPassword)){
            return null;
        }

        // Set new password and save.
        String passwordNew = encoder.encode(beforeNewPassword);
        employee.setPasswordHash(passwordNew);
        employeeRepository.save(employee);
        return employee;
    }
}
