package com.javateam.mgep.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.javateam.mgep.constants.MailConstaints;
import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.repositories.AdminRepository;
import com.javateam.mgep.repositories.EmployeeRepository;
import com.javateam.mgep.service.AdminService;
import com.javateam.mgep.service.MailService;

@Service
public class AdminServiceImpl implements AdminService {

	private final Logger log = LoggerFactory.getLogger(AdminService.class);

	private final JavaMailSender javaMailSender;
	private final MessageSource messageSource;
	private final SpringTemplateEngine templateEngine;

	public AdminServiceImpl(JavaMailSender javaMailSender, MessageSource messageSource,
			SpringTemplateEngine templateEngine) {
		this.javaMailSender = javaMailSender;
		this.messageSource = messageSource;
		this.templateEngine = templateEngine;
	}

	@Autowired
	AdminRepository adminRepository;
	
	@Autowired
	EmployeeRepository employeeRepository;

	@Override
	public List<Employee> searchEmployee(String email) {
		List<Employee> list = new ArrayList<Employee>();
		list = adminRepository.findByEmail(email);
		return list;
	}

	@Override
	public void sendEmail(String email, String title, String content, boolean interval, String value) {
		Optional<Employee> employee = employeeRepository.findOneByEmailIgnoreCase(email);
		if(employee.isPresent()) {
			Locale locale = new Locale("vi", "VN");
			Context context = new Context(locale);
			context.setVariable("title", title);
			context.setVariable("content", content);
			String context1 = templateEngine.process("mail/adminEmail", context);
			String[] to = { email };
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			try {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, StandardCharsets.UTF_8.name());
				message.setTo(to);
				message.setFrom(MailConstaints.FROM);
				message.setSubject(title);
				message.setText(context1, true);
				javaMailSender.send(mimeMessage);
				System.out.println("sent mail success");
			} catch (MailException | MessagingException e) {
				log.warn("Email could not be sent to user '{}'", to, e);
			}
		}
		else {
			System.out.println("no mail found");
		}


	}

}
