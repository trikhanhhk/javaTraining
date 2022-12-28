package com.javateam.mgep.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.javateam.mgep.entity.CustomUserDetails;
import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.service.AdminService;

@Controller
public class AdminController {
	
	@Autowired
	AdminService adminService;
	
    @GetMapping("/admin/home")
    public String HomeAdmin(Model model, HttpSession session, String keyword){
    	System.out.println(keyword);
    	if(keyword == null) {
    		List<Employee> list = adminService.searchEmployee("");
    		model.addAttribute("list",list);
    	}
    	else {
    		List<Employee> list = adminService.searchEmployee(keyword);
    		model.addAttribute("list",list);
    	}
        return "/admin/home";
    }
    
    
}
