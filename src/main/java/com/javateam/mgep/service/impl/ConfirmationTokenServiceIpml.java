package com.javateam.mgep.service.impl;

import com.javateam.mgep.entity.ConfirmationToken;
import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.repositories.ConfirmationTokenRepository;
import com.javateam.mgep.repositories.EmployeeRepository;
import com.javateam.mgep.service.ConfirmationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfirmationTokenServiceIpml implements ConfirmationTokenService {
    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    EmployeeRepository employeeRepository;

    @Override
    public Employee getEmployeeByToken(String tokenId) {
        ConfirmationToken token  = confirmationTokenRepository.findByConfirmationToken(tokenId);
        if(token != null) {
            Employee employee = employeeRepository.findOneByEmailIgnoreCase(token.getUserEntity().getEmail()).get();
            employee.setStatus(1);
            employeeRepository.save(employee);
            return employee;
        } else {
            return null;
        }
    }
}
