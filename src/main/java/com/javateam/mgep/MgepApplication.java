package com.javateam.mgep;

import com.javateam.mgep.entity.Department;
import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@SpringBootApplication
@EnableJpaRepositories("com.javateam.mgep.repositories")
public class MgepApplication {

	public static void main(String[] args) {
		SpringApplication.run(MgepApplication.class, args);
	}

	@Autowired
	EmployeeRepository userRepository;
	@Autowired
	PasswordEncoder passwordEncoder;

//	@Override
//	public void run(String... args) throws Exception {
//		// Khi chương trình chạy
//		// Insert vào csdl một user.
//		Employee employee = new Employee();
//		employee.setEmail("abcd@gmail.com");
//		employee.setPasswordHash(passwordEncoder.encode("12345678"));
//		employee.setAddress("abc abc");
//		employee.setGender("1");
//		employee.setFirstName("abc");
//		employee.setLastName("def");
//		employee.setPhoneNumber("93842347");
//		DateTimeFormatter df = DateTimeFormatter.ofPattern("d-MMM-yyyy");
//		employee.setDateOfBirth(LocalDate.parse("01-Jan-2017", df));
//		employee.setStatus("1");
//		employee.setId(2L);
//		userRepository.save(employee);
//		System.out.println(employee);
//	}

}
