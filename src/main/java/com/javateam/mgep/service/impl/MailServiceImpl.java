package com.javateam.mgep.service.impl;

import com.javateam.mgep.constants.MailConstaints;
import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class MailServiceImpl implements MailService {

    private final Logger log = LoggerFactory.getLogger(MailService.class);

    private static final String USER = "user";
    private static final String BASE_URL = "baseUrl";

    private static final String KEY_ACTIVE = "keyActive";

    private static final String PASSWORD_NEW_ACCOUNT = "password";

    private final JavaMailSender javaMailSender;
    private final MessageSource messageSource;
    private final SpringTemplateEngine templateEngine;

    public MailServiceImpl(
            JavaMailSender javaMailSender,
            MessageSource messageSource,
            SpringTemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
    }

    @Override
    @Async
    public void sendMail(String[] to, String subject, String content, boolean isMultipart, boolean isHtml) {
        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, "UTF-8");
            message.setTo(to);
            message.setFrom(MailConstaints.FROM);
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            log.debug("Sent email to User '{}'", to);
        } catch (MailException | MessagingException e) {
            log.warn("Email could not be sent to user '{}'", to, e);
        }
    }

    @Async
    public void sendMailFromTemplate(Employee employee, String keyActive, String template, String titleKey) {
        if (employee.getEmail() == null) {
            log.debug("Email doesn't exist for user '{}'", employee.getEmail());
            return;
        }
        Locale locale = new Locale("vi", "VN");
        Context context = new Context(locale);
        context.setVariable(USER, employee);
        context.setVariable(KEY_ACTIVE, keyActive);
        context.setVariable(BASE_URL, MailConstaints.BASE_URL);
        String content = templateEngine.process(template, context);
        String subject = messageSource.getMessage(titleKey, null, locale);
        String[] to = {employee.getEmail()};
        sendMail(to, subject, content, false, true);
    }

    @Async
    public void sendMailPasswordNewAcount(Employee employee, String pasword, String template, String titleKey) {
        if (employee.getEmail() == null) {
            log.debug("Email doesn't exist for user '{}'", employee.getEmail());
            return;
        }
        Locale locale = new Locale("vi", "VN");
        Context context = new Context(locale);
        context.setVariable(USER, employee);
        context.setVariable(PASSWORD_NEW_ACCOUNT, pasword);
        context.setVariable(BASE_URL, MailConstaints.BASE_URL);
        String content = templateEngine.process(template, context);
        String subject = messageSource.getMessage(titleKey, null, locale);
        String[] to = {employee.getEmail()};
        sendMail(to, subject, content, false, true);
    }

    @Override
    @Async
    public void sendActiveMail(Employee employee, String keyActive) {
        log.debug("Sending activation email to '{}'", employee.getEmail());
        sendMailFromTemplate(employee, keyActive,"mail/activationEmail", "email.activation.title");
    }

    @Override
    @Async
    public void sendPasswordResetMail(Employee employee, String keyActive) {
        log.debug("Sending activation email to '{}'", employee.getEmail());
        sendMailFromTemplate(employee, keyActive,"mail/passwordResetEmail", "email.reset.title");
    }

    @Override
    public void sendPasswordNewAcount(Employee employee, String password) {
        sendMailPasswordNewAcount(employee, password, "mail/passwordNewAccount", "email.password.title");
    }
}
