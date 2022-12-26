package com.javateam.mgep.controller;

import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.entity.ModelData;
import com.javateam.mgep.service.ForgotPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

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
        return "forgotPassword";
    }

    @PostMapping("/forgotPassword")
    public String sendMail(Model model, @RequestParam("email")String email) throws Exception {
        try {
            String result = forgotPasswordService.sendEmailForgotPassword(email);
            model.addAttribute("message", "Bạn vui lòng check email để lấy lấy lại mật khẩu");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "forgotPassword";
    }

    @GetMapping("/resetPassword")
    public String resetPassword(Model model, @RequestParam("key") String token) {
        model.addAttribute("token", token);
        return "/password/resetPassword";
    }

    @PostMapping("/resetPassword")
    public String handleResetPassword(Model model,
                                      @RequestParam("newPassword") String newPassword,
                                      @RequestParam("repeatPassword") String repeatPassword,
                                      @RequestParam("token") String token) throws Exception{
        try {
            Employee employee = forgotPasswordService.resetPasswordByToken(newPassword, repeatPassword, token);
            ModelData modelData = new ModelData();
            modelData.setMessage("Mật khẩu đã được thay đổi, vui lòng đăng nhập lại với mật khẩu mới");
            HashMap<String, String> data = new HashMap<>();
            data.put("email", employee.getEmail());
            data.put("password", newPassword);
            modelData.setData(data);
            model.addAttribute("param", modelData);
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "resetPassword";
        }
    }
}
