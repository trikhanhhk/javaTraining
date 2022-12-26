package com.javateam.mgep.controller;

import com.javateam.mgep.entity.CustomUserDetails;
import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.service.ChangePasswordService;
import com.javateam.mgep.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class WebController {

    @Autowired
    ChangePasswordService changePasswordService;

    @Autowired
    EmployeeService employeeService;

    @GetMapping(value = {"/", "/home"})
    public String homepage() {
        return "home";
    }

    @GetMapping("/hello")
    public String hello(Model model, HttpSession session) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        CustomUserDetails userDetails = (CustomUserDetails) securityContext.getAuthentication().getPrincipal();
        String fullName = userDetails.getEmployee().getFirstName() + " " + userDetails.getEmployee().getLastName();
        Object employee = session.getAttribute("employee");
        if (employee == null){
            model.addAttribute("fullName", fullName);
            model.addAttribute("employee", userDetails.getEmployee());
        }else{
            model.addAttribute("fullName", fullName);
            model.addAttribute("employee", employee);
        }
        Object update = session.getAttribute("flags");
        Object failedUpdate = session.getAttribute("updateFailed");
        if (update != null){
            model.addAttribute("flags",update);
        }
        if (failedUpdate != null){
            model.addAttribute("failedUpdate",failedUpdate );
        }
        return "home";
    }
    @GetMapping("/update")
    public String getUpdateUser(HttpSession session){
        String flags = "update";
        session.setAttribute("flags",flags);
        return "redirect:hello";
    }

    @PostMapping("/submit-update")
    public String submitUpdate(@RequestParam("address") String address,
                                @RequestParam("phoneNumber") String phone,
                               @RequestParam("email") String email, HttpSession session){
        Employee employee = employeeService.updateEmployee(address,phone,email);
        if (employee != null){
            session.setAttribute("flags",null);
            session.setAttribute("employee",employee);
            return "redirect:/hello";
        }
        session.setAttribute("flags","updateFailed");
        return "redirect:update";
    }

    @GetMapping("/cancel")
    public String cancelUpdateUser(HttpSession session){
        session.setAttribute("flags", null);
        return "redirect:hello";
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
