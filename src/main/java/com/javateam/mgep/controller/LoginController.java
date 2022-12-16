package com.javateam.mgep.controller;

import com.javateam.mgep.service.ForgotPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    @Autowired
    ForgotPasswordService forgotPasswordService;
    @GetMapping("/login")
    public String Login(){
        return "login";
    }

    @GetMapping("/forgotPassword")
    public String senEmail(){
        return "/password/quenmatkhau";
    }

    @PostMapping("/forgotPassword")
    public String sendMail(Model model, @RequestParam("email")String email) throws Exception {
        try {
            String result = forgotPasswordService.sendEmailForgotPassword(email);
            model.addAttribute("message", "Bạn vui lòng check email để lấy lấy lại mật khẩu");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "/password/quenmatkhau";
    }
}
