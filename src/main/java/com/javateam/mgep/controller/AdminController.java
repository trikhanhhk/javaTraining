package com.javateam.mgep.controller;

import com.javateam.mgep.entity.*;
import com.javateam.mgep.entity.dto.EmailDataForm;
import com.javateam.mgep.entity.dto.EmployeeData;
import com.javateam.mgep.repositories.*;
import com.javateam.mgep.service.DepartmentService;
import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.entity.dto.SearchCriteria;
import com.javateam.mgep.service.EmployeeService;
import com.javateam.mgep.service.SendMailService;
import com.javateam.mgep.service.excel.ExcelGeneratorListEmployee;
import com.javateam.mgep.service.impl.AuthorityServiceIpml;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
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
import java.util.Set;

@Controller
public class AdminController {
    @Autowired
    SendMailService sendMailService;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    DepartmentService departmentService;
    @Autowired
    AuthorityServiceIpml authorityService;
    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    AuthorityRepository authorityRepository;
    @Autowired
    DepartmentRepository departmentRepository;

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

        //Get authenticate permissions
        List<GrantedAuthority> grantList = (List<GrantedAuthority>) userDetails.getAuthorities();
        for (GrantedAuthority x : grantList) {
            session.setAttribute("role", x.getAuthority());
        }
        String errorEmail = (String) session.getAttribute("errorEmail");
        String errorNull = (String) session.getAttribute("error");
        String message = (String) session.getAttribute("message");
        if (errorNull != null) {
            model.addAttribute("error", errorNull);
        }
        if (errorEmail != null) {
            System.out.println(errorEmail);
            model.addAttribute("error", errorEmail);
        } else if (message != null) {
            System.out.println(message);
            model.addAttribute("message", message);
        }

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
        DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=DS_NV_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<Employee> listOfStudents = employeeService.getListAll();
        ExcelGeneratorListEmployee generator = new ExcelGeneratorListEmployee(listOfStudents);
        generator.generateExcelFile(response);
    }


    //Displays screen import-to-excel
    @RequestMapping(value = "/admin/import-to-excel", method = RequestMethod.POST)
    public String importExcelFile(@RequestParam("file") MultipartFile files, Model model, HttpSession session) {
        try {
            List<Employee> lstEmployee = employeeService.importFileEx(files);
            session.removeAttribute("error");
            session.removeAttribute("message");
            session.removeAttribute("errorEmail");

            //Import-to-excel false.
            if (lstEmployee == null) {
                String error = "Lỗi file! File của bạn đang rỗng!";
                session.setAttribute("error", error);
                return "redirect:/adminHome";
            }

            //Import-to-excel successful.
            String message = "Import File thành công!";
            session.setAttribute("message", message);
            SecurityContext securityContext = SecurityContextHolder.getContext();
            CustomUserDetails userDetails = (CustomUserDetails) securityContext.getAuthentication().getPrincipal();
            String fullName = userDetails.getEmployee().getFirstName() + " " + userDetails.getEmployee().getLastName();
            List<Employee> employeeList = employeeService.getListAll();

            //Set full name user login for session
            session.setAttribute("fullName", fullName);

            //Save information for model
            model.addAttribute("fullName", fullName);
            model.addAttribute("employeeList", employeeList);
        } catch (Exception e) {
            String errorEmail = "Đã có lỗi xảy ra trong quá trình import, vui lòng kiểm tra lại định dạng file theo đúng chuẩn mẫu";
            session.setAttribute("errorEmail", errorEmail);
            e.printStackTrace();
        }

        return "redirect:/adminHome";
    }


    //Function searchEmployee
    @GetMapping("/admin/searchEmployee")
    public String searchEmployee(@Validated @ModelAttribute("searchCriteria") SearchCriteria search, Model model, HttpSession session) {
        session.removeAttribute("error");
        session.removeAttribute("message");
        session.removeAttribute("errorEmail");
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
    public String deleteEmployee(@PathVariable("id") Long id, Model model,HttpSession session) {
        session.removeAttribute("error");
        session.removeAttribute("message");
        session.removeAttribute("errorEmail");
        String result = employeeService.deleteEmployeeById(id);
        if (result == null) {
            model.addAttribute("error", "Không tìm thấy nhân viên");
            return "redirect:adminHome";
        }
        return "redirect:adminHome";
    }


    //Displays screen update information employee.
    @GetMapping("/admin/update/{id}")
    public String updateAdmin(@PathVariable("id") long id, Model model, HttpSession session) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        List<Department> departments = departmentService.getListDept();
        List<Authoritty> authorityList = authorityService.findByAllAuthoritty();

        Set<Authoritty> authorittySet = employee.getAuthorities();
        String role = null;
        for (Authoritty authoritty : authorittySet) {
            role = authoritty.getName();
        }
        //Get information to session saved.
        String fullName = (String) session.getAttribute("fullName");
        String message = (String) session.getAttribute("messageUpdate");
        System.out.println(employee.getDateOfBirth());
        model.addAttribute("message", message);
        model.addAttribute("role", role);
        session.setAttribute("employee", employee);
        model.addAttribute("name", fullName);
        model.addAttribute("employee", employee);
        model.addAttribute("departments", departments);

        session.removeAttribute("error");
        session.removeAttribute("message");
        session.removeAttribute("errorEmail");
        session.removeAttribute("messageUpdate");
        model.addAttribute("authorityList",authorityList);
        session.setAttribute("message", null);
        return "/admin/update";
    }


    //Submit button update information employee.
    @PostMapping("/admin/submit-update-admin")
    public String submitUpdate(Model model, @ModelAttribute("employee") EmployeeData employeeData, @RequestParam("role") String role, HttpSession session) {
        session.removeAttribute("error");
        session.removeAttribute("message");
        session.removeAttribute("errorEmail");
        Employee employee = employeeService.updateEmployeeAdmin(employeeData,role);

        if (employee == null) {
            model.addAttribute("error", "Cập nhật thất bại!!!");
            return "redirect:adminHome/" + employeeData.getId();
        }

        session.setAttribute("messageUpdate", "Cập nhật thành công");
        return "redirect:update/" + employeeData.getId();
    }


    //Displays screen send email admin
    @GetMapping("/admin/send-email")
    public String sendEmailAdmin(HttpSession session, Model model) {
        session.removeAttribute("error");
        session.removeAttribute("message");
        session.removeAttribute("errorEmail");
        //Get information to session saved.
        String fullName = (String) session.getAttribute("fullName");
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("name", fullName);

        return "/admin/sendEmail";
    }

    @PostMapping("/admin/send-email")  //xử lý gửi mail
    public String sendEmailAdmin(HttpSession session, Model model, @Validated @ModelAttribute("emailData") EmailDataForm emailDataForm) throws SchedulerException {
        session.removeAttribute("error");
        session.removeAttribute("message");
        session.removeAttribute("errorEmail");
        String fullName = (String) session.getAttribute("fullName");
        model.addAttribute("name", fullName);
        model.addAttribute("message", "Đã gửi email thành công");
        sendMailService.handleSendMail(emailDataForm);  //Gọi sang service gửi mail
        return "/admin/sendEmail";
    }

}
