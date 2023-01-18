package com.javateam.mgep.service;

import com.javateam.mgep.constants.CommonConstants;
import com.javateam.mgep.entity.ConfirmationToken;
import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.entity.ResetPasswordToken;
import com.javateam.mgep.repositories.ConfirmationTokenRepository;
import com.javateam.mgep.repositories.EmployeeRepository;
import com.javateam.mgep.repositories.ResetPasswordTokenRepository;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public class TimeJob  extends QuartzJobBean {
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    ConfirmationTokenService confirmationTokenService;

    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    ResetPasswordTokenRepository resetPasswordTokenRepository;
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        String jobName = jobDataMap.getString(CommonConstants.JOB_NAME);  //lấy ra job name
        if(CommonConstants.JOB_ACCOUNT_IS_ACTIVE.equals(jobName)) {
            Long idEmployee =jobDataMap.getLongValue(CommonConstants.ID_DATA_JOB);
            Optional<Employee> employee = employeeRepository.findById(idEmployee);
            employee.ifPresent(e -> {
                if("0".equals(e.getStatus())) {  // Nếu chưa được kích hoạt thì sẽ xóa tài khoản
                    employeeService.deleteEmployeeById(e.getId());
                }
            });
        } else if (CommonConstants.JOB_RESET_PASSWORD_TOKEN.equals(jobName)) {
            Long idReset = jobDataMap.getLongValue(CommonConstants.ID_DATA_JOB);
            Optional<ResetPasswordToken> resetPasswordToken = resetPasswordTokenRepository.findById(idReset);
            resetPasswordToken.ifPresent(token -> {
                resetPasswordTokenRepository.delete(token);
            });
        }
    }
}
