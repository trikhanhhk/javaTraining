package com.javateam.mgep.service.impl;

import com.javateam.mgep.entity.Department;
import com.javateam.mgep.entity.EmailData;
import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.entity.dto.EmailDataForm;
import com.javateam.mgep.repositories.DepartmentRepository;
import com.javateam.mgep.repositories.EmailDataRepository;
import com.javateam.mgep.repositories.EmployeeRepository;
import com.javateam.mgep.repositories.SendMailRepository;
import com.javateam.mgep.service.EmailJob;
import com.javateam.mgep.service.MailService;
import com.javateam.mgep.service.SendMailService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;
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
    EmailDataRepository emailDataRepository;
    @Autowired
    MailService mailService;

    @Autowired
    private Scheduler scheduler;

    @Override
    public void handleSendMail(EmailDataForm emailDataForm) throws SchedulerException {
        EmailData emailData = new EmailData(emailDataForm);
        String repeat = emailData.getRepeat();  //Định kỳ hay không định kỳ
        String typeSend = emailData.getTypeSend();  //loại mail gửi (phòng ban, tất cả, )
        if ("1".equals(repeat)) {  //Gửi định kỳ ngày tuần, tháng
            try {
                emailData.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse(emailDataForm.getStartDate()));
                emailData.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse(emailDataForm.getEndDate()));  //ngày kết thúc gửi
            } catch (ParseException parseException) {
                throw new RuntimeException(parseException);
            }
            if ("1".equals(emailData.getRepeatType())) {  //gửi dịnh kỳ hằng ngày
                List<Date> dates = new ArrayList<Date>();
                long interval = 24 * 1000 * 60 * 60; // thời gian của 1 ngày
                long endTime = emailData.getEndDate().getTime();
                long curTime = emailData.getStartDate().getTime();
                while (curTime <= endTime) {
                    Date dateSend = new Date(curTime);
                    dateSend.setHours(Integer.parseInt(emailData.getTimeSend().split(":")[0]));
                    dateSend.setMinutes(Integer.parseInt(emailData.getTimeSend().split(":")[1]));
                    emailData.setDateSend(dateSend);
                    emailData.setDateSendTime(new Date(curTime));
                    emailData.setCreateDate(new Date());
                    EmailData emailDataSave = new EmailData(emailData);
                    String sendTo = "";
                    if ("3".equals(typeSend)) { //gửi cho tất cả
                        sendTo = getSendToAll(emailData);
                    } else if (typeSend.equals("1")) { //gửi theo phòng ban
                        sendTo = getSendToGroup(emailData);
                    }
                    emailDataSave.setSendTo(sendTo);
                    emailDataSave.setStatus("0");
                    EmailData email = emailDataRepository.save(emailDataSave);
                    scheduleTaskWithFixedRate(email);
                    curTime += interval;  // Cộng thêm 1 ngày
                }
            } else if ("2".equals(emailData.getRepeatType())) { //định kỳ hàng tuần
                long interval = 7 * 24 * 1000 * 60 * 60; // Thời gian 7 ngày
                long endTime = emailData.getEndDate().getTime();
                long curTime = emailData.getStartDate().getTime();
                while (curTime <= endTime) {
                    Date dateSend = new Date(curTime);
                    dateSend.setHours(Integer.parseInt(emailData.getTimeSend().split(":")[0]));
                    dateSend.setMinutes(Integer.parseInt(emailData.getTimeSend().split(":")[1]));
                    emailData.setDateSend(dateSend);
                    emailData.setDateSendTime(new Date(curTime));
                    EmailData emailDataSave = new EmailData(emailData);
                    emailData.setCreateDate(new Date());
                    String sendTo = "";
                    if ("3".equals(typeSend)) { //gửi cho tất cả
                        sendTo = getSendToAll(emailData);
                    } else if (typeSend.equals("1")) { //gửi theo phòng ban
                        sendTo = getSendToGroup(emailData);
                    }
                    emailDataSave.setSendTo(sendTo);
                    emailDataSave.setStatus("0");
                    EmailData email = emailDataRepository.save(emailDataSave);
                    scheduleTaskWithFixedRate(email);
                    curTime += interval;  //Công thêm 7 ngày
                }
            } else if ("3".equals(emailData.getRepeatType())) { //định kỳ hàng tháng
                Date curTime = emailData.getStartDate();
                Date endTime = emailData.getEndDate();
                while (curTime.compareTo(endTime) > 0) {
                    Date dateSend = curTime;
                    dateSend.setHours(Integer.parseInt(emailData.getTimeSend().split(":")[0]));
                    dateSend.setMinutes(Integer.parseInt(emailData.getTimeSend().split(":")[1]));
                    emailData.setDateSend(dateSend);
                    emailData.setDateSendTime(curTime);
                    EmailData emailDataSave = new EmailData(emailData);
                    emailData.setCreateDate(new Date());
                    String sendTo = "";
                    if ("3".equals(typeSend)) { //gửi cho tất cả
                        sendTo = getSendToAll(emailData);
                    } else if (typeSend.equals("1")) { //gửi theo phòng ban
                        sendTo = getSendToGroup(emailData);
                    }
                    emailDataSave.setSendTo(sendTo);
                    emailDataSave.setStatus("0");
                    EmailData email = emailDataRepository.save(emailDataSave);
                    scheduleTaskWithFixedRate(email);
                    curTime = getNextMonth(curTime);
                }
            }
        } else {  // Gửi mail 1 lần, không định kỳ
            if ("3".equals(typeSend)) { //gửi cho tất cả
                this.sendEmailToAll(emailData);
            } else if (typeSend.equals("1")) { //gửi theo phòng ban
                this.sendEmailToGroup(emailData);
            }
        }

    }

    //lấy ra ngày ngày của tháng sau
    public Date getNextMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        if (calendar.get(Calendar.MONTH) == Calendar.DECEMBER) {
            calendar.set(Calendar.MONTH, Calendar.JANUARY);
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
        } else {
            calendar.roll(Calendar.MONTH, true);
        }

        return calendar.getTime();
    }

    public String getSendToGroup(EmailData emailData) {
        String sendTo = "";
        String deptId = emailData.getDeptId();
        List<Employee> employeesDept = employeeRepository.findAll(); //list nhân viên
        Optional<Department> department = departmentRepository.findById(Long.parseLong(deptId));  //List phòng ban
        if (department.isPresent()) {
            List<Employee> result = employeesDept.stream()
                    .filter(employee -> deptId.equals(employee.getDepartment().getId().toString()))
                    .collect(Collectors.toList());  //lấy ra nhân viên thuộc phòng ban đã chọn
            for (int i = 0; i < result.size(); i++) {  //lấy ra danh sách gửi
                sendTo += result.get(i).getEmail();
                if (i < result.size() - 1) {
                    sendTo += ",";
                }
            }
        }
        return sendTo;
    }

    //lấy danh sách gửi mail tới tất cả
    public String getSendToAll(EmailData emailData) {
        List<Employee> employees = employeeRepository.findAll();
        String sendTo = "";
        for (int i = 0; i < employees.size(); i++) {
            sendTo += employees.get(i).getEmail();
            if (i < employees.size() - 1) {
                sendTo += ",";
            }
        }
        return sendTo;
    }

    //gửi mail theo group
    public void sendEmailToGroup(EmailData emailData) {
        String sendTo = getSendToGroup(emailData);
        emailData.setSendTo(sendTo);  //update danh sách gửi
        this.send(emailData); //gửi mail
        emailData.setStatus("1");
        emailDataRepository.save(emailData);

    }

    //Gửi cho tất cả
    public void sendEmailToAll(EmailData emailData) {
        String sendTo = getSendToAll(emailData);
        emailData.setSendTo(sendTo);
        emailData.setStatus("1");
        emailDataRepository.save(emailData);
        this.send(emailData);
    }

    //Taọ lich gửi mail
    public void scheduleTaskWithFixedRate(EmailData e) throws SchedulerException {
        JobDetail jobDetail = buildJobDetail(e);
        Trigger trigger = buildJobTrigger(jobDetail, e.getDateSend());
        scheduler.scheduleJob(jobDetail, trigger);
    }

    private JobDetail buildJobDetail(EmailData emailData) {
        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("idEmailData", emailData.getId());  //Truyền ID mail để gửi

        return JobBuilder.newJob(EmailJob.class)
                .withIdentity(UUID.randomUUID().toString(), "email-jobs")
                .withDescription("Send Email Job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail, Date startAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "email-triggers")
                .withDescription("Send Email Trigger")
                .startAt(startAt)  //thời gian gửi
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }


    public void send(EmailData emailData) {
        mailService.sendMail(emailData.getSendTo().split(","), emailData.getSubject(), emailData.getContent(), false, false); //gọi sang service gửi mail
    }
}
