package com.javateam.mgep.service;

import com.javateam.mgep.entity.Employee;
import org.springframework.scheduling.annotation.Async;

public interface MailService {
    @Async
    public void sendMail(String[] to, String subject, String content, boolean isMultipart, boolean isHtml);
    @Async
    public void sendActiveMail(Employee employee, String keyActive);
    @Async
    public void sendPasswordResetMail(Employee employee, String keyAtive);

    @Async
    public void sendPasswordNewAcount(Employee employee, String password);
}
