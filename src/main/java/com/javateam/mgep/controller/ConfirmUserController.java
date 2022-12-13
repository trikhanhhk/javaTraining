package com.javateam.mgep.controller;

import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.service.ConfirmationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ConfirmUserController {

    @Autowired
    ConfirmationTokenService confirmationTokenService;

    @GetMapping(value="/confirm-account")
    public ModelAndView confirmUserAccount(ModelAndView modelAndView, @RequestParam("key")String confirmationToken)
    {

        Employee employee = confirmationTokenService.getEmployeeByToken(confirmationToken);
        if(employee != null){
            modelAndView.setViewName("accountVerified");
        } else{
            modelAndView.addObject("message","The link is invalid or broken!");
//            modelAndView.setViewName("error");
        }

        return modelAndView;
    }
}
