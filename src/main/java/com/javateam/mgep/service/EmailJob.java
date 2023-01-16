package com.javateam.mgep.service;

import com.javateam.mgep.entity.EmailData;
import com.javateam.mgep.repositories.EmailDataRepository;
import com.javateam.mgep.service.impl.SendMailServiceIpml;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.logging.Logger;

@Component
public class EmailJob extends QuartzJobBean {
//    private static final Logger logger = (Logger) LoggerFactory.getLogger(EmailJob.class);
    @Autowired
    private SendMailServiceIpml sendMailServiceIpml;
    @Autowired
    private EmailDataRepository emailDataRepository;


    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//        logger.info("Executing Job with key {}", jobExecutionContext.getJobDetail().getKey());

        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        Long id = jobDataMap.getLongValue("idEmailData");
        Optional<EmailData> emailData = emailDataRepository.findById(id);
        emailData.ifPresent(email -> {
            if ("3".equals(email.getTypeSend())) { //gửi cho tất cả
                sendMailServiceIpml.sendEmailToAll(email);
            } else if ("1".equals(email.getTypeSend())) { //gửi theo phòng ban
                sendMailServiceIpml.sendEmailToGroup(email);
            }
        });
    }
}
