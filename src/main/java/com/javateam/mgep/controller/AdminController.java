package com.javateam.mgep.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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
    
    @GetMapping("/admin/mail")
    public String EmailAdmin(Model model, HttpSession session){

        return "/admin/mail";
    }
    
    @PostMapping("/admin/mail-sent")
    public String EmailAdminSent(Model model, HttpSession session, String email, String title, String content, boolean interval, String value){
    	System.out.println(email);
    	System.out.println(title);
    	System.out.println(content);
    	System.out.println(interval);
    	System.out.println(value);
    	adminService.sendEmail(email, title, content, interval, value);
        return "redirect:mail";
    }
    
    
}
