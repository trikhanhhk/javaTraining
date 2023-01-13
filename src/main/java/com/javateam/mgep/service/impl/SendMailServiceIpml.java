package com.javateam.mgep.service.impl;

import com.javateam.mgep.entity.Department;
import com.javateam.mgep.entity.EmailData;
import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.repositories.DepartmentRepository;
import com.javateam.mgep.repositories.EmailDataRepository;
import com.javateam.mgep.repositories.EmployeeRepository;
import com.javateam.mgep.repositories.SendMailRepository;
import com.javateam.mgep.service.MailService;
import com.javateam.mgep.service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SendMailServiceIpml extends TimerTask implements SendMailService {
    @Autowired
    SendMailRepository sendMailRepository;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    EmailDataRepository emailDataRepository;
    @Autowired
    MailService mailService;

    @Override
    public void handleSendMail(EmailData emailData) {
        String repeat = emailData.getRepeat();
        String deptId = emailData.getDeptId();
        String typeSend = emailData.getTypeSend();  //loại mail gửi (phòng ban, tất cả, )
        if("1".equals(repeat)) {

        } else {  // Gửi mail 1 lần, không định kỳ
            if ("3".equals(typeSend)) { //gửi cho tất cả
                List<Employee> employees = employeeRepository.findAll();
                String sendTo = "";
                for (int i = 0; i < employees.size(); i++) {
                    sendTo += employees.get(i).getEmail();
                    if (i < employees.size() - 1) {
                        sendTo += ",";
                    }
                }
                emailData.setSendTo(sendTo);
                this.send(emailData);
            } else if (typeSend.equals("1")) { //gửi theo phòng ban
                List<Employee> employeesDept = employeeRepository.findAll(); //list nhân viên
                Optional<Department> department = departmentRepository.findById(Long.parseLong(deptId));  //List phòng ban
                if (department.isPresent()) {
                    List<Employee> result = employeesDept.stream()
                            .filter(employee -> department.get().equals(employee.getDepartment()))
                            .collect(Collectors.toList());  //lấy ra nhân viên thuộc phòng ban đã chọn
                    String sendTo = "";
                    for (int i = 0; i < result.size(); i++) {  //lấy ra danh sách gửi
                        sendTo += result.get(i).getEmail();
                        if (i < result.size() - 1) {
                            sendTo += ",";
                        }
                    }
                    emailData.setSendTo(sendTo);  //update danh sách gửi
                    this.send(emailData); //gửi mail
                }
            }
        }

    }

    //1 phut kiểm tra 1 lần để gửi mail định kỳ
    @Scheduled(fixedRate = 1000*60)
    public void scheduleTaskWithFixedRate() {
        List<EmailData> emailDataList = emailDataRepository.findEmailDataByRepeat();
        Date current = new Date();
        if(emailDataList!=null) {
            for (EmailData e : emailDataList) {
                Date startDate = e.getStartDate();
                if("1".equals(e.getRepeatType())) {
                    this.send(e);
                } else if("2".equals(e.getRepeatType())) {
                    Date startDateWeek = e.getStartDate();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(startDateWeek);

//                    while (true) {
//                        calendar = calendar.c
//                        if ()
//                    }
                } else if("3".equals(e.getRepeatType())) {

                }
            }
        }
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

    @Override
    public void run() {

    }
}
