package com.javateam.mgep.service.impl;

import com.javateam.mgep.entity.EmailData;
import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.repositories.EmployeeRepository;
import com.javateam.mgep.repositories.SendMailRepository;
import com.javateam.mgep.service.MailService;
import com.javateam.mgep.service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.ejb.Schedule;
import java.lang.reflect.Array;
import java.util.Date;
import java.util.List;

@Service
public class SendMailServiceIpml implements SendMailService {
    @Autowired
    SendMailRepository sendMailRepository;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    MailService mailService;
    String cron = "";
    @Override
    public void sendMail(EmailData emailData) {
        String type = emailData.getType();
        String typeSend = emailData.getTypeSend();
        if(typeSend.equals("3")) {
            List<Employee> employees = employeeRepository.findAll();
            String sendTo = "";
            for (int i = 0; i<employees.size(); i++) {
                sendTo += employees.get(i).getEmail();
                if(i<employees.size()-1) {
                    sendTo += ",";
                }
            }
            emailData.setSendTo(sendTo);
            this.send(emailData);
        }

    }
//    @Scheduled(cron = "0 8 * * *")
//    public void sendMailDay(EmailData emailData) {
//        this.sendMail(emailData);
//    }
//
//    @Scheduled(cron = "0 8 1 * *")
//    public void sendMailMonth(EmailData emailData) {
//        this.sendMail(emailData);
//    }
//
//    @Scheduled(cron = "0 8 1 * *")
//    public void sendMailWeek(EmailData emailData) {
//        this.sendMail(emailData);
//    }

    public void send(EmailData emailData) {
        mailService.sendMail(emailData.getSendTo().split(","), emailData.getSubject(), emailData.getContent(), false, false);
    }

    @Override
    public EmailData saveEmailData(EmailData emailData) {

        return null;
    }

    @Override
    public EmailData updateEmailData(EmailData emailData) {
        return null;
    }

    @Override
    public void deleteEmailData(Long id) {

    }
}
