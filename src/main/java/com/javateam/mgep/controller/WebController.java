package com.javateam.mgep.controller;

import com.javateam.mgep.entity.CustomUserDetails;
import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.service.ChangePasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {

    @Autowired
    ChangePasswordService changePasswordService;
    @GetMapping(value = {"/", "/home"})
    public String homepage() {
        return "home";
    }

    @GetMapping("/hello")
    public String hello(Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        CustomUserDetails userDetails = (CustomUserDetails) securityContext.getAuthentication().getPrincipal();
        String fullName = userDetails.getEmployee().getFirstName() + " " + userDetails.getEmployee().getLastName();
        model.addAttribute("fullName", fullName);
        model.addAttribute("employee", userDetails.getEmployee());
        return "home";
    }

    @GetMapping("/email")
    public String senEmail() {
        return "/password/quenmatkhau";
    }

    @GetMapping("/hello/new-password")
    public String newPassword() {
        return "password/doimatkhau";
    }

    @PostMapping("/hello/submit-new-password")
    public String submitNewPassword(@RequestParam("oldPassword") String oldPassword,
                                    @RequestParam("newPassword") String newPassword,
                                    @RequestParam("newPassword1") String newPassword1) {
        Employee employee = changePasswordService.changePassword(oldPassword,newPassword,newPassword1);
        if (employee != null){
            return "successful";
        }
        return "failed";
    }
}
