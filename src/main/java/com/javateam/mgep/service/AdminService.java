package com.javateam.mgep.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.javateam.mgep.entity.Employee;

public interface AdminService {
	public List<Employee> searchEmployee(String email);

}
