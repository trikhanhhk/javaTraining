package com.javateam.mgep.controller;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.javateam.mgep.entity.CustomUserDetails;
import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.repositories.EmployeeRepository;
import com.javateam.mgep.service.AdminService;

@Controller
public class AdminController {
	
	@Autowired
	AdminService adminService;
	
	@Autowired
	EmployeeRepository employeeRepository;
	
    @GetMapping("/admin/home")
    public String HomeAdmin(Model model, HttpSession session, String keyword){
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
    
    @GetMapping("/admin/update/{id}")
    public String UpdateAdmin(@PathVariable("id") long id,Model model, HttpSession session){
    	Employee employee = employeeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
    		model.addAttribute("employee",employee);

        return "/admin/update";
    }
    @PostMapping("/admin/submit-update")
    public String SubmitUpdateAdmin(Model model, HttpSession session, String email,String firstName,String lastName, String dateOfBirth, String phoneNumber, String address, Long id){
    	System.out.println(dateOfBirth);
    	try {
			adminService.updateAdmin(id, email, firstName, lastName, dateOfBirth, phoneNumber, address);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return "redirect:home";
    }
    
    @GetMapping("/admin/mail")
    public String EmailAdmin(Model model, HttpSession session){

        return "/admin/mail";
    }
    
    @PostMapping("/admin/mail-sent")
    public String EmailAdminSent(Model model, HttpSession session, String email, String title, String content, String interval, String value){
    	
    	if(value != null && interval == "") {
    		int parseinterval = Integer.parseInt(value);
    		if (parseinterval > 0) {
        		Timer timer = new Timer();
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                    	adminService.sendEmail(email, title, content, interval, value);
                    }
                }, 0, parseinterval * 86400000);
    		}

    	}
    	else {
        	adminService.sendEmail(email, title, content, interval, value);
    	}

        return "redirect:mail";
    }
    
    @PostMapping("/admin/export")
    public String ExportAdmin(HttpServletResponse servletResponse) throws IOException{
    	System.out.println("");
        servletResponse.setContentType("text/csv");
        servletResponse.addHeader("Content-Disposition","attachment; filename=\"employees.csv\"");
        adminService.exportAdmin(servletResponse.getWriter());


        return "redirect:home";
    }
    
    @RequestMapping(value="/admin/import",method = RequestMethod.POST)
    public String ImportAdmin(InputStream body) throws IOException, ParseException{
    	System.out.println("");
    	adminService.importAdmin();


        return "redirect:home";
    }
    
    
}
