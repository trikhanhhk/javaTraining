package com.javateam.mgep.controller;

import com.javateam.mgep.entity.*;
import com.javateam.mgep.entity.dto.EmployeeData;
import com.javateam.mgep.repositories.*;
import com.javateam.mgep.service.DepartmentService;
import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.entity.dto.SearchCriteria;
import com.javateam.mgep.service.EmployeeService;
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

    //Displays screen Home Admin
    @GetMapping({"/adminHome", "/admin"})
    public String homeAdmin(Model model, HttpSession session) {
        //Get information login to security.
        SecurityContext securityContext = SecurityContextHolder.getContext();
        CustomUserDetails userDetails = (CustomUserDetails) securityContext.getAuthentication().getPrincipal();
        String fullName = userDetails.getEmployee().getFirstName() + " " + userDetails.getEmployee().getLastName();
        List<Employee> employeeList = employeeService.getListAll();

        //Set full name user login for session
        session.setAttribute("fullName", fullName);

        //Save information for model
        model.addAttribute("fullName", fullName);
        model.addAttribute("employeeList", employeeList);
        return "admin/home";
    }


    //Displays screen export-to-excel
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


    //Displays screen import-to-excel
    @RequestMapping(value = "/admin/import-to-excel", method = RequestMethod.POST)
    public String importExcelFile(@RequestParam("file") MultipartFile files,Model model){
        List<Employee> lstEmployee = employeeService.importFileEx(files);

        //Import-to-excel false.
        if (lstEmployee == null){
            model.addAttribute("error","Không nhập được file");
            return "redirect:/admin/home";
        }

        //Import-to-excel successful.
        model.addAttribute("OK","Nhập File Thành công!");

        return "redirect:/admin/home";
    }


    //Function searchEmployee
    @GetMapping("/admin/searchEmployee")
    public String searchEmployee(@Validated @ModelAttribute("searchCriteria") SearchCriteria search, Model model, HttpSession session) {

        //Get information login to security.
        SecurityContext securityContext = SecurityContextHolder.getContext();
        CustomUserDetails userDetails = (CustomUserDetails) securityContext.getAuthentication().getPrincipal();
        List<Employee> employeeList = employeeService.searchByData(search);

        String fullName = userDetails.getEmployee().getFirstName() + " " + userDetails.getEmployee().getLastName();

        session.setAttribute("fullName", fullName);
        model.addAttribute("fullName", fullName);
        model.addAttribute("employeeList", employeeList);
        return "admin/home";
    }


    //Displays screen delete information employee.
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


    //Displays screen update information employee.
    @GetMapping("/admin/update/{id}")
    public String updateAdmin(@PathVariable("id") long id, Model model, HttpSession session) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        List<Department> departments = departmentService.getListDept();

        //Get information to session saved.
        String fullName = (String) session.getAttribute("fullName");

        session.setAttribute("employee", employee);
        model.addAttribute("departments",departments);
        model.addAttribute("employee", employee);
        model.addAttribute("name", fullName);

        return "/admin/update";
    }


    //Submit button update information employee.
    @PostMapping("/admin/submit-update-admin")
    public String submitUpdate(Model model,@ModelAttribute("employee") EmployeeData employeeData) {
        Employee employee = employeeService.updateEmployeeAdmin(employeeData);

        if (employee == null){
            model.addAttribute("error","Cập nhập thất bại!!!");
            return "redirect:adminHome/"+ employeeData.getId();
        }

        return "redirect:/adminHome";
    }


    //Displays screen send email admin
    @GetMapping("/admin/send-email")
    public  String sendEmailAdmin(HttpSession session,Model model){

        //Get information to session saved.
        String fullName = (String) session.getAttribute("fullName");
        model.addAttribute("name", fullName);

        return "/admin/sendEmail";
    }

}
