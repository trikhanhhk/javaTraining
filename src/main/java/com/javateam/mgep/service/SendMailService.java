package com.javateam.mgep.service;

import com.javateam.mgep.entity.EmailData;
import com.javateam.mgep.entity.dto.EmailDataForm;

public interface SendMailService {
    public void handleSendMail(EmailDataForm emailDataForm);

    public EmailData saveEmailData(EmailData emailData);

    public EmailData updateEmailData(EmailData emailData);

    public void deleteEmailData(Long id);


}
