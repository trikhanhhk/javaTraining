package com.javateam.mgep.controller;

import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.entity.ModelData;
import com.javateam.mgep.entity.dto.PasswordResetData;
import com.javateam.mgep.service.ForgotPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Controller
public class LoginController {
    @Autowired
    ForgotPasswordService forgotPasswordService;

    //Displays screen login.
    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/forgotPassword")
    public String senEmail(){
        return "password/forgotPassword";
    }

    @PostMapping("/forgotPassword")
    public String sendMail(Model model, @RequestParam("email")String email) throws Exception {
        try {
            forgotPasswordService.sendEmailForgotPassword(email);
            model.addAttribute("message", "Bạn vui lòng check email để lấy lấy lại mật khẩu");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "password/forgotPassword";
    }

    @GetMapping("/resetPassword")
    public String resetPassword(Model model, @RequestParam("key") String token) {
        model.addAttribute("token", token);
        return "password/resetPassword";
    }

    @PostMapping("/resetPassword")
    @ResponseBody
    public ResponseEntity<ModelData> handleResetPassword(@Validated @ModelAttribute("dataPassword")PasswordResetData dataPassword) throws Exception{
        ModelData modelData = new ModelData();
        try {
            Employee employee = forgotPasswordService.resetPasswordByToken(dataPassword.getNewPassword(), dataPassword.getRepeatPassword(), dataPassword.getToken());
            modelData.setMessage("Mật khẩu đã được thay đổi, vui lòng đăng lại với mật khẩu mới");
            HashMap<String, String> data = new HashMap<>();
            data.put("email", employee.getEmail());
            data.put("password", dataPassword.getNewPassword());
            modelData.setData(data);
            return ResponseEntity.ok(modelData);
        } catch (RuntimeException e) {
            modelData.setError(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(modelData);
        }
    }
}
