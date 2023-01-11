package com.javateam.mgep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableJpaRepositories("com.javateam.mgep.repositories")
public class MgepApplication {

	public static void main(String[] args) {
		SpringApplication.run(MgepApplication.class, args);
	}

}
