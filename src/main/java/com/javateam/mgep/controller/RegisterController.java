package com.javateam.mgep.controller;

import com.javateam.mgep.entity.Department;
import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.entity.dto.EmployeeData;
import com.javateam.mgep.service.DepartmentService;
import com.javateam.mgep.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class RegisterController {
    @Autowired
    DepartmentService departmentService;

    @Autowired
    EmployeeService employeeService;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("employee", new EmployeeData());
        List<Department> departments = departmentService.getListDept();
        model.addAttribute("departments", departments);
        return "register";
    }

    @PostMapping("/addEmployee")
    public String doRegister(@ModelAttribute("employee") EmployeeData employee, ModelMap modelMap) {
        Employee newEmployee = employeeService.addEmployee(employee);
        return "login";
    }

}
