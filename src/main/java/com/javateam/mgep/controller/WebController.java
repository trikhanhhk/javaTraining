package com.javateam.mgep.controller;

import com.javateam.mgep.entity.CustomUserDetails;
import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.entity.ModelData;
import com.javateam.mgep.entity.dto.PasswordResetData;
import com.javateam.mgep.service.ChangePasswordService;
import com.javateam.mgep.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class WebController {

    @Autowired
    ChangePasswordService changePasswordService;

    @Autowired
    EmployeeService employeeService;


    //Displays screen home user.
    @GetMapping(value = {"/", "/home","/hello"})
    public String getHome(Model model, HttpSession session) {

        //Get information user login to security.
        SecurityContext securityContext = SecurityContextHolder.getContext();
        CustomUserDetails userDetails = (CustomUserDetails) securityContext.getAuthentication().getPrincipal();

        //Get information after update.
        Employee employee = (Employee) session.getAttribute("employee");

        model.addAttribute("message", null);
        // Get role user login.
        List<GrantedAuthority> grantList = (List<GrantedAuthority>) userDetails.getAuthorities();
        for (GrantedAuthority x : grantList) {
            if (x.getAuthority().equals("ROLE_ADMIN")){
                model.addAttribute("admin","admin");
            }
        }

        String fullName = userDetails.getEmployee().getFirstName() + " " + userDetails.getEmployee().getLastName();
        if (employee == null){
            model.addAttribute("fullName", fullName);
            model.addAttribute("employee", userDetails.getEmployee());
        }else{
            model.addAttribute("fullName", fullName);
            model.addAttribute("employee", employee);
        }
        String update = (String) session.getAttribute("flags");
        String message = (String) session.getAttribute("message");
        String failedUpdate = (String) session.getAttribute("updateFailed");

        if (update != null){
            model.addAttribute("flags",update);
        }
        if (message != null) {
        	model.addAttribute("message", message);
        }
        if (failedUpdate != null){
            model.addAttribute("failedUpdate",failedUpdate );
        }
        return "home";
    }

    //Displays screen update user.
    @GetMapping("/update")
    public String getUpdateUser(HttpSession session){
        String flags = "update";
        session.setAttribute("flags",flags);
        return "redirect:hello";
    }


    // Submit form update information user.
    @PostMapping("/submit-update")
    public String submitUpdate(@RequestParam("address") String address,
                                @RequestParam("phoneNumber") String phone,
                               @RequestParam("email") String email, HttpSession session, Model model){
        Employee employee = employeeService.updateEmployee(address,phone,email);

        if (employee != null){
            session.setAttribute("flags",null);
            session.setAttribute("employee",employee);
            session.setAttribute("message","Cập nhật thành công");
            return "redirect:/hello";
        }
        session.setAttribute("flags","updateFailed");
        return "redirect:update";
    }

    // Cancel update information to screen user.
    @GetMapping("/cancel")
    public String cancelUpdateUser(HttpSession session){
        session.setAttribute("flags", null);
        return "redirect:hello";
    }

    //Go to displays screen changePassword user.
    @GetMapping("/hello/new-password")
    public String newPassword(Model model , HttpSession session) {
        String success = (String) session.getAttribute("success");
        String failed = (String) session.getAttribute("failed");
        if (success != null) {
            model.addAttribute("success", success);
            session.setAttribute("success", null);
        }
        if (failed != null) {
            model.addAttribute("failed", failed);
            session.setAttribute("failed", null);
        }

        return "password/changePasswordUser";
    }


    //displays screen changePassword handle user submit
    @PostMapping("/hello/submit-new-password")
    public String submitNewPassword(@Validated @ModelAttribute("dataPassword") PasswordResetData dataPassword, HttpSession session) {
        String employee = changePasswordService.changePassword(dataPassword.getOldPassword(),dataPassword.getNewPassword(), dataPassword.getRepeatPassword());

        if (employee.equalsIgnoreCase("Đổi mật khẩu thành công")){
            session.setAttribute("success", employee);
            return "redirect:/hello/new-password";
        }else {
        	session.setAttribute("failed", employee);
            return "redirect:/hello/new-password";
        }
    }

}
