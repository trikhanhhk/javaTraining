package com.javateam.mgep.controller;

import com.javateam.mgep.entity.CustomUserDetails;
import com.javateam.mgep.entity.Employee;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {
    @GetMapping(value = {"/", "/home"})
    public String homepage() {
        return "home";
    }

    @GetMapping("/hello")
    public String hello(Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        CustomUserDetails userDetails = (CustomUserDetails) securityContext.getAuthentication().getPrincipal();
        String fullName = userDetails.getEmployee().getFirstName() + " "+ userDetails.getEmployee().getLastName();
        System.out.println(userDetails.getEmployee().getDateOfBirth());
        model.addAttribute("fullName",fullName);
        model.addAttribute("employee",userDetails.getEmployee());
        return "home";
    }
}
