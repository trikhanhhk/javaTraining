package com.javateam.mgep.service;

import com.javateam.mgep.entity.Employee;

public interface ForgotPasswordService {

    /**
     * *
     * @param email
     * @return Email address employee
     */
    public String sendEmailForgotPassword(String email) throws Exception;

    /**
     * *
     * @param token
     * @return
     */
    public Employee resetPasswordByToken(String newPassword, String repeatPassword, String token);
}
