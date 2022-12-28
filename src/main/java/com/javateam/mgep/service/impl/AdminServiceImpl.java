package com.javateam.mgep.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.repositories.AdminRepository;
import com.javateam.mgep.service.AdminService;

@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	AdminRepository adminRepository;
	
	@Override
	public List<Employee> searchEmployee(String email) {
		List<Employee> list = new ArrayList<Employee>();
		list = adminRepository.findByEmail(email);
		return list;
	}

}
