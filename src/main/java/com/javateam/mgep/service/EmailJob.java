package com.javateam.mgep.service;

import com.javateam.mgep.entity.EmailData;
import com.javateam.mgep.repositories.EmailDataRepository;
import com.javateam.mgep.service.impl.SendMailServiceIpml;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EmailJob extends QuartzJobBean {
    @Autowired
    private SendMailServiceIpml sendMailServiceIpml;
    @Autowired
    private EmailDataRepository emailDataRepository;


    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        Long id = jobDataMap.getLongValue("idEmailData");  //Lấy ra ID email cần gửi
        Optional<EmailData> emailData = emailDataRepository.findById(id);  //Lất ra email cần gửi
        emailData.ifPresent(email -> {  //Nếu tồn tại email thì xử lý
            if ("3".equals(email.getTypeSend())) { //gửi cho tất cả
                sendMailServiceIpml.sendEmailToAll(email);
            } else if ("1".equals(email.getTypeSend())) { //gửi theo phòng ban
                sendMailServiceIpml.sendEmailToGroup(email);
            }
        });
    }
}
