package com.javateam.mgep.service.impl.service;

import com.javateam.mgep.repositories.EmployeeRepository;
import com.javateam.mgep.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class ForgotPasswordServiceImplTest {
    private EmployeeRepository employeeRepository = Mockito.mock(EmployeeRepository.class);
    @Test
    void sendEmailForgotPassword() {
    }
}
