package com.javateam.mgep.controller;

import com.javateam.mgep.entity.*;
import com.javateam.mgep.entity.dto.EmployeeData;
import com.javateam.mgep.repositories.*;
import com.javateam.mgep.service.DepartmentService;
import com.javateam.mgep.service.EmployeeService;
import com.javateam.mgep.service.excel.ExcelGeneratorListEmployee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class AdminController {
    @Autowired
    EmployeeService employeeService;
    @Autowired
    DepartmentService departmentService;
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    AuthorityRepository authorityRepository;
    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    ResetPasswordTokenRepository resetPasswordTokenRepository;

    @GetMapping({"/admin/home", "/admin/", "/admin"})
    public String homeAdmin(Model model, HttpSession session) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        CustomUserDetails userDetails = (CustomUserDetails) securityContext.getAuthentication().getPrincipal();
        String fullName = userDetails.getEmployee().getFirstName() + " " + userDetails.getEmployee().getLastName();
        model.addAttribute("fullName", fullName);
        session.setAttribute("fullName", fullName);
        List<Employee> employeeList = employeeService.getListAll();
        model.addAttribute("employeeList", employeeList);
        return "/homeAdmin/home";
    }

    @GetMapping("/admin/export-to-excel")
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

    @RequestMapping(value = "/admin/import-to-excel", method = RequestMethod.POST)
    public String importExcelFile(@RequestParam("file") MultipartFile files) throws IOException {

        return null;
    }

    @GetMapping("/{id}")
    public String deleteEmployee(@PathVariable("id") String id, Model model) {
        Optional<Employee> employeeFindById = employeeRepository.findById(Long.parseLong(id));
        if (employeeFindById.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy nhân viên");
            return "redirect:admin/home";
        }
        Employee employee = employeeFindById.get();
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByUserEntity(employee.getId());
        System.out.println(confirmationToken);
        if (confirmationToken != null) {
            confirmationTokenRepository.deleteById(confirmationToken.getTokenid());
        }

        List<ResetPasswordToken> resetPasswordToken = resetPasswordTokenRepository.findByUserEntity(employee);
        int count = 0;
        for (int i = 0; i < resetPasswordToken.size(); i++) {
            resetPasswordTokenRepository.deleteById(resetPasswordToken.get(i).gettkenId());
            count++;
        }
        employeeRepository.delete(employee);
        return "redirect:admin/home";
    }

    @GetMapping("/admin/update/{id}")
    public String updateAdmin(@PathVariable("id") long id, Model model, HttpSession session) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        model.addAttribute("employee", employee);
        String fullName = (String) session.getAttribute("fullName");
        System.out.println(fullName);
        List<Department> departments = departmentService.getListDept();
        model.addAttribute("departments",departments);
        session.setAttribute("employee", employee);

        model.addAttribute("name", fullName);
        return "/homeAdmin/update";
    }

    @PostMapping("/admin/submit-update-admin")
    public String submitUpdate(Model model,@ModelAttribute("employee") EmployeeData employeeData) {
        Employee employee = employeeService.updateEmployeeAdmin(employeeData);
        System.out.println(employeeData.getId());
        if (employee == null){
            model.addAttribute("error","Cập nhập thất bại!!!");
            return "redirect:admin/home/"+ employeeData.getId();
        }

        return "redirect:/admin/home";
    }

}
