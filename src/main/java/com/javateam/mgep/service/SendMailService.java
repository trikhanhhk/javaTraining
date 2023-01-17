package com.javateam.mgep.service;

import com.javateam.mgep.entity.EmailData;
import com.javateam.mgep.entity.dto.EmailDataForm;
import org.quartz.SchedulerException;

public interface SendMailService {
    public void handleSendMail(EmailDataForm emailDataForm) throws SchedulerException;

    public EmailData saveEmailData(EmailData emailData);

    public EmailData updateEmailData(EmailData emailData);

    public void deleteEmailData(Long id);


}
