package com.javateam.mgep.controller;

import com.javateam.mgep.entity.Department;
import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.entity.ModelData;
import com.javateam.mgep.entity.dto.EmployeeData;
import com.javateam.mgep.service.DepartmentService;
import com.javateam.mgep.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

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
    @ResponseBody
    public ResponseEntity<ModelData> doRegister(@ModelAttribute("employee") EmployeeData employee) throws Exception {
        ModelData modelData = new ModelData();
        try {
            Employee newEmployee = employeeService.addEmployee(employee);
            if(newEmployee != null) {
                modelData.setMessage("Tài khoản đã được đăng ký vui lòng kiểm tra email để xác nhận đăng ký");
            } else {
                modelData.setError("Đã có lỗi xảy ra xin vui lòng thử lại");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(modelData);
            }
            return ResponseEntity.ok(modelData);
        }catch (RuntimeException exception){
            modelData.setError(exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(modelData);
        }
    }
}
