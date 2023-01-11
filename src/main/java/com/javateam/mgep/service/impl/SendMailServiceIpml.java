package com.javateam.mgep.service.impl;

import com.javateam.mgep.entity.Department;
import com.javateam.mgep.entity.EmailData;
import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.repositories.DepartmentRepository;
import com.javateam.mgep.repositories.EmployeeRepository;
import com.javateam.mgep.repositories.SendMailRepository;
import com.javateam.mgep.service.MailService;
import com.javateam.mgep.service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SendMailServiceIpml implements SendMailService {
    @Autowired
    SendMailRepository sendMailRepository;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    MailService mailService;
    @Override
    public void sendMail(EmailData emailData) {
        String deptId = emailData.getDeptId();
        String typeSend = emailData.getTypeSend();  //loại mail gửi (phòng ban, tất cả, )
        if(typeSend.equals("3")) { //gửi cho tất cả
            List<Employee> employees = employeeRepository.findAll();
            String sendTo = "";
            for (int i = 0; i<employees.size(); i++) {
                sendTo += employees.get(i).getEmail();
                if(i<employees.size()-1) {
                    sendTo += ",";
                }
            }
            emailData.setSendTo(sendTo);
            this.send(emailData);
        } else if(typeSend.equals("1")) { //gửi theo phòng ban
            List<Employee> employeesDept = employeeRepository.findAll(); //list nhân viên
            Optional<Department> department = departmentRepository.findById(Long.parseLong(deptId));  //List phòng ban
            if(department.isPresent()) {
                List<Employee> result = employeesDept.stream()
                        .filter(employee -> department.get().equals(employee.getDepartment()))
                        .collect(Collectors.toList());  //lấy ra nhân viên thuộc phòng ban đã chọn
                String sendTo = "";
                for (int i = 0; i<result.size(); i++) {  //lấy ra danh sách gửi
                    sendTo += result.get(i).getEmail();
                    if(i<result.size()-1) {
                        sendTo += ",";
                    }
                }
                emailData.setSendTo(sendTo);  //update danh sách gửi
                this.send(emailData); //gửi mail
            }
        }

        this.saveEmailData(emailData); //lưu lại mail đã gửi

    }



    public void send(EmailData emailData) {
        mailService.sendMail(emailData.getSendTo().split(","), emailData.getSubject(), emailData.getContent(), false, false);
    }

    @Override
    public EmailData saveEmailData(EmailData emailData) {
        emailData.setCreateDate(new Date());
        return sendMailRepository.save(emailData); //Lưu lại thông tin email vào db
    }

    @Override
    public EmailData updateEmailData(EmailData emailData) {
        return null;
    }

    @Override
    public void deleteEmailData(Long id) {

    }
}
