package com.javateam.mgep.controller;

import com.javateam.mgep.entity.Department;
import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.entity.dto.EmployeeData;
import com.javateam.mgep.service.DepartmentService;
import com.javateam.mgep.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class RegisterController {
    @Autowired
    DepartmentService departmentService;

    @Autowired
    EmployeeService employeeService;

    @InitBinder
    public void initBinder(WebDataBinder binder){
        binder.registerCustomEditor( Date.class,
                new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true, 10));
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("employee", new EmployeeData());
        List<Department> departments = departmentService.getListDept();
        model.addAttribute("departments", departments);
        model.addAttribute("radio","true");
        return "register";
    }

    @PostMapping("/addEmployee")
    public String doRegister(@ModelAttribute("employee") EmployeeData employee, Model model) throws Exception {
        try {
            Employee newEmployee = employeeService.addEmployee(employee);
            if(newEmployee != null) {
                return "login";
            } else {
                return "redirect:register?error=?";
            }
        }catch (RuntimeException exception){
            model.addAttribute("error", exception.getMessage());
            return "redirect:register?error=?";
        }
    }
}
