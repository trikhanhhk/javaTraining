package com.javateam.mgep.service.impl;

import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.entity.ResetPasswordToken;
import com.javateam.mgep.exception.PasswordNotMatchException;
import com.javateam.mgep.repositories.EmployeeRepository;
import com.javateam.mgep.repositories.ResetPasswordTokenRepository;
import com.javateam.mgep.service.ForgotPasswordService;
import com.javateam.mgep.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {
    @Autowired
    ResetPasswordTokenRepository resetPasswordTokenRepository;

    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    MailService mailService;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    public ForgotPasswordServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String sendEmailForgotPassword(String email) throws RuntimeException {
        Optional<Employee> employeeExist = employeeRepository.findOneByEmailIgnoreCase(email);
        if(employeeExist.isPresent()) {
            ResetPasswordToken resetPasswordToken = new ResetPasswordToken(employeeExist.get());
            resetPasswordTokenRepository.save(resetPasswordToken);
            mailService.sendPasswordResetMail(employeeExist.get(), resetPasswordToken.getResetPasswordToken());
            return email;
        } else {
            throw new RuntimeException("Không tồn tại email trong hệ thống");
        }
    }

    @Override
    public Employee resetPasswordByToken(String newPassword, String repeatPassword, String token) throws RuntimeException{
        ResetPasswordToken resetToken = resetPasswordTokenRepository.findByResetPasswordToken(token);
        if(resetToken != null) {
            Employee employee = resetToken.getUserEntity();
            if(!newPassword.equals(repeatPassword)) {
                throw new PasswordNotMatchException();
            }
            employee.setPasswordHash(passwordEncoder.encode(newPassword));
            return employeeRepository.save(employee);
        } else {
            throw new RuntimeException("Đường dẫn không đúng hoặc hết thời hạn");
        }
    }
}
