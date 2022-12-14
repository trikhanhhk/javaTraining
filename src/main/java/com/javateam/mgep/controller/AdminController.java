package com.javateam.mgep.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {
    @GetMapping("/admin/home")
    public String HomeAdmin(){
        return "/admin/home";
    }
}
