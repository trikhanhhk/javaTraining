package com.javateam.mgep.service;

import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.javateam.mgep.entity.Employee;

public interface AdminService {
	public List<Employee> searchEmployee(String email);
	
	public void sendEmail(String email, String title, String content, String interval, String value );
	
	public void updateAdmin(Long id, String email, String firstName, String lastName, String dateOfBirth, String phoneNumber, String address) throws ParseException;
	
	public void exportAdmin(Writer writer);
	
	public void importAdmin() throws IOException, ParseException;

}
