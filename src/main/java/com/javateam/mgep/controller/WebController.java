package com.javateam.mgep.controller;

import com.javateam.mgep.entity.Authoritty;
import com.javateam.mgep.entity.CustomUserDetails;
import com.javateam.mgep.entity.Employee;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Set;

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
        Set<Authoritty> authoritties = userDetails.getEmployee().getAuthorities();
        System.out.println(authoritties.size());
        model.addAttribute("fullName",fullName);
        model.addAttribute("employee",userDetails.getEmployee());
        return "home";
    }

    @GetMapping("/email")
    public String senEmail(){
        return "/password/quenmatkhau";
    }
}
