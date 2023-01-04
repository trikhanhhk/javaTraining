package com.javateam.mgep.controller;

import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.service.EmployeeService;
import com.javateam.mgep.service.excel.ExcelGeneratorListEmployee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class AdminController {
    @Autowired
    EmployeeService employeeService;
    @GetMapping("/admin/home")
    public String homeAdmin(){
        return "/admin/home";
    }

    @GetMapping("/export-to-excel")
    public void exportIntoExcelFile(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=DS_NV" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<Employee> listOfStudents = employeeService.getListAll();
        ExcelGeneratorListEmployee generator = new ExcelGeneratorListEmployee(listOfStudents);
        generator.generateExcelFile(response);
    }
}
