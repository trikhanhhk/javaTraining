package com.javateam.mgep.service.impl;

import com.javateam.mgep.constants.CommonConstants;
import com.javateam.mgep.entity.ConfirmationToken;
import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.entity.ResetPasswordToken;
import com.javateam.mgep.exception.PasswordNotMatchException;
import com.javateam.mgep.repositories.EmployeeRepository;
import com.javateam.mgep.repositories.ResetPasswordTokenRepository;
import com.javateam.mgep.service.ForgotPasswordService;
import com.javateam.mgep.service.MailService;
import com.javateam.mgep.service.TimeJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {
    @Autowired
    MailService mailService;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    ResetPasswordTokenRepository resetPasswordTokenRepository;

    @Autowired
    Scheduler scheduler;

    public ForgotPasswordServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    //Function send email forgot Password.
    public String sendEmailForgotPassword(String email) throws RuntimeException, SchedulerException {
        Optional<Employee> employeeExist = employeeRepository.findOneByEmailIgnoreCase(email);
        if(employeeExist.isPresent()) {
            ResetPasswordToken resetPasswordToken = new ResetPasswordToken(employeeExist.get());
            resetPasswordTokenRepository.save(resetPasswordToken);
            mailService.sendPasswordResetMail(employeeExist.get(), resetPasswordToken.getResetPasswordToken());
            scheduleTaskWithFixedRate(resetPasswordToken, new Date(new Date().getTime() + 2000 * 60));
            return email;
        } else {
            throw new RuntimeException("Không tồn tại email trong hệ thống");
        }
    }

    @Override
    //Reset Password by Token.
    public Employee resetPasswordByToken(String newPassword, String repeatPassword, String token) throws RuntimeException{
        ResetPasswordToken resetToken = resetPasswordTokenRepository.findByResetPasswordToken(token);
        if(resetToken != null) {
            Employee employee = resetToken.getUserEntity();
            if(!newPassword.equals(repeatPassword)) {
                throw new PasswordNotMatchException();
            }
            employee.setPasswordHash(passwordEncoder.encode(newPassword));
            return employeeRepository.save(employee);
        } else {
            throw new RuntimeException("Đường dẫn không đúng hoặc hết thời hạn");
        }
    }

    public void scheduleTaskWithFixedRate(ResetPasswordToken token, Date satrtAt) throws SchedulerException {
        JobDetail jobDetail = buildJobDetail(token);
        Trigger trigger = buildJobTrigger(jobDetail, satrtAt);
        scheduler.scheduleJob(jobDetail, trigger);
    }

    private JobDetail buildJobDetail(ResetPasswordToken token) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(CommonConstants.JOB_NAME, CommonConstants.JOB_RESET_PASSWORD_TOKEN);
        jobDataMap.put(CommonConstants.ID_DATA_JOB, token.gettkenId());  //Truyền ID job để gửi

        return JobBuilder.newJob(TimeJob.class)
                .withIdentity(UUID.randomUUID().toString(), "tken-jobs")
                .withDescription("Check token")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail, Date startAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "token-triggers")
                .withDescription("Check token")
                .startAt(startAt)  //thời gian gửi
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }
}
