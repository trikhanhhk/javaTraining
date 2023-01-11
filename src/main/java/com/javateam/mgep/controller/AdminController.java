package com.javateam.mgep.controller;

import com.javateam.mgep.entity.*;
import com.javateam.mgep.entity.dto.EmployeeData;
import com.javateam.mgep.repositories.*;
import com.javateam.mgep.service.DepartmentService;
import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.entity.dto.SearchCriteria;
import com.javateam.mgep.service.EmployeeService;
import com.javateam.mgep.service.SendMailService;
import com.javateam.mgep.service.excel.ExcelGeneratorListEmployee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

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
    @Autowired
    SendMailService sendMailService;

    @GetMapping({"/adminHome", "/admin"})
    public String homeAdmin(Model model, HttpSession session) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        CustomUserDetails userDetails = (CustomUserDetails) securityContext.getAuthentication().getPrincipal();
        String fullName = userDetails.getEmployee().getFirstName() + " " + userDetails.getEmployee().getLastName();
        model.addAttribute("fullName", fullName);
        session.setAttribute("fullName", fullName);
        List<Employee> employeeList = employeeService.getListAll();
        model.addAttribute("employeeList", employeeList);
        return "admin/home";
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
    public String importExcelFile(@RequestParam("file") MultipartFile files,Model model){
        System.out.println("haha");
        List<Employee> lstEmpoloyee = employeeService.importFileEx(files);
        if (lstEmpoloyee == null){
            model.addAttribute("error","Không nhập được file");
            return "redirect:/admin/home";
        }
        model.addAttribute("OK","Nhập File Thành công!");
        return "redirect:/admin/home";
    }

    @GetMapping("/admin/searchEmployee")
    public String searchEmployee(@Validated @ModelAttribute("searchCriteria") SearchCriteria search, Model model, HttpSession session) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        CustomUserDetails userDetails = (CustomUserDetails) securityContext.getAuthentication().getPrincipal();
        String fullName = userDetails.getEmployee().getFirstName() + " " + userDetails.getEmployee().getLastName();
        model.addAttribute("fullName", fullName);
        session.setAttribute("fullName", fullName);
        List<Employee> employeeList = employeeService.searchByData(search);
        model.addAttribute("employeeList", employeeList);
        return "admin/home";
    }

    @GetMapping("/{id}")
    public String deleteEmployee(@PathVariable("id") Long id, Model model) {
        Optional<Employee> employeeFindById = employeeRepository.findById(id);
        if (employeeFindById.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy nhân viên");
            return "redirect:admin/home";
        }
        Employee employee = employeeFindById.get();
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByUserEntity(employee.getId());
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
        return "redirect:adminHome";
    }

    @GetMapping("/admin/update/{id}")
    public String updateAdmin(@PathVariable("id") long id, Model model, HttpSession session) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        model.addAttribute("employee", employee);
        String fullName = (String) session.getAttribute("fullName");
        List<Department> departments = departmentService.getListDept();
        model.addAttribute("departments",departments);
        session.setAttribute("employee", employee);

        model.addAttribute("name", fullName);
        return "/admin/update";
    }

    @PostMapping("/admin/submit-update-admin")
    public String submitUpdate(Model model,@ModelAttribute("employee") EmployeeData employeeData) {
        Employee employee = employeeService.updateEmployeeAdmin(employeeData);
        System.out.println(employeeData.getId());
        if (employee == null){
            model.addAttribute("error","Cập nhập thất bại!!!");
            return "redirect:adminHome/"+ employeeData.getId();
        }
        return "redirect:/adminHome";
    }

    @GetMapping("/admin/send-email")
    public  String sendEmailAdmin(HttpSession session,Model model){
        String fullName = (String) session.getAttribute("fullName");
        model.addAttribute("name", fullName);
        return "/admin/sendEmail";
    }

    @PostMapping("/admin/send-email")
    public String sendEmailAdmin(HttpSession session, Model model, @Validated @ModelAttribute("emailData") EmailData emailData) {
        String fullName = (String) session.getAttribute("fullName");
        model.addAttribute("name", fullName);
        model.addAttribute("message", "Đã gửi mail");
        sendMailService.sendMail(emailData);
        return "/admin/sendEmail";
    }

}
