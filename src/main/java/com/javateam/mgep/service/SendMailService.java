package com.javateam.mgep.service;

import com.javateam.mgep.entity.EmailData;

public interface SendMailService {
    public void handleSendMail(EmailData emailData);

    public EmailData saveEmailData(EmailData emailData);

    public EmailData updateEmailData(EmailData emailData);

    public void deleteEmailData(Long id);


}
