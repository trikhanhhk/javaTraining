package com.javateam.mgep.service.impl;

import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
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
import com.javateam.mgep.entity.dto.EmployeeData;
import com.javateam.mgep.repositories.AdminRepository;
import com.javateam.mgep.repositories.EmployeeRepository;
import com.javateam.mgep.service.AdminService;
import com.javateam.mgep.service.EmployeeService;
import com.javateam.mgep.service.MailService;
import com.opencsv.CSVReader;

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
	EmployeeService employeeService;
	
	@Autowired
	EmployeeRepository employeeRepository;

	@Override
	public List<Employee> searchEmployee(String email) {
		List<Employee> list = new ArrayList<Employee>();
		list = adminRepository.findByEmail(email);
		return list;
	}

	@Override
	public void sendEmail(String email, String title, String content, String interval, String value) {
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

	@Override
	public void updateAdmin(Long id, String email, String firstName, String lastName, String dateOfBirth,
			String phoneNumber, String address) throws ParseException {
		Employee employee = adminRepository.findOneById(id);
		if (employee != null) {
			employee.setEmail(email);
			employee.setFirstName(firstName);
			employee.setLastName(lastName);
			Date date1 =new SimpleDateFormat("yyyy-MM-dd").parse(dateOfBirth);
			employee.setDateOfBirth(date1);
			employee.setPhoneNumber(phoneNumber);
			employee.setAddress(address);
			employeeRepository.save(employee);
			System.out.println("update thành công!");
		}
		else {
			System.out.println("update thất bại");
		}
		
	}

	@Override
	public void exportAdmin(Writer writer) {
		List<Employee> employees = employeeRepository.findAll();
        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            for (Employee employee : employees) {
                csvPrinter.printRecord(employee.getId(), employee.getFirstName(), employee.getLastName(),employee.getGender(),employee.getDateOfBirth().toString(), employee.getEmail(), employee.getAddress(), employee.getPhoneNumber(), employee.getDepartment().getName());
            }
        } catch (IOException e) {
            log.error("Error While writing CSV ", e);
        }
		
	}

	@Override
	public void importAdmin() throws IOException, ParseException {
		CSVReader reader = new CSVReader(new FileReader("D:\\emps.csv"), ',');

		List<EmployeeData> emps = new ArrayList<EmployeeData>();

		// read line by line
		String[] record = null;

		while ((record = reader.readNext()) != null) {
			EmployeeData emp = new EmployeeData();
			emp.setFirstName(record[0]);
			emp.setLastName(record[1]);
			emp.setDateOfBirth(record[2]);
			emp.setPhoneNumber(record[3]);
			emp.setGender(record[4]);
			emp.setAddress(record[5]);
			emp.setEmail(record[6]);
			emp.setPassword(record[7]);
			emp.setRepeatPassword(record[8]);
			emp.setDeptId((long) 1);
			System.out.println(emp.getGender());
			employeeService.addEmployee(emp);
			

		}

		System.out.println(emps);
		
		reader.close();
		
	}

}
