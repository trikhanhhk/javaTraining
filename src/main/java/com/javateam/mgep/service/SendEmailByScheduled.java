package com.javateam.mgep.service;

import com.javateam.mgep.entity.EmailData;
import com.javateam.mgep.repositories.EmailDataRepository;
import com.javateam.mgep.service.impl.SendMailServiceIpml;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class SendEmailByScheduled {
    @Autowired
    EmailDataRepository emailDataRepository;
    @Autowired
    SendMailServiceIpml sendMailServiceIpml;
    @Autowired
    private Scheduler scheduler;

    //1 ngày sẽ chạy kiểm tra 1 lần vào lúc 7h AM phut kiểm tra 1 lần để gửi mail định kỳ
    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleTaskWithFixedRate() throws SchedulerException {
        List<EmailData> emailDataList = emailDataRepository.findEmailDataByRepeat();
        if(emailDataList!=null) {
            for (EmailData e : emailDataList) {
                Date dateSend = e.getDateSend();
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                if(formatter.format(new Date()).equals(formatter.format(dateSend))) {
                    JobDetail jobDetail = buildJobDetail(e);
                    Trigger trigger = buildJobTrigger(jobDetail, e.getDateSend());
                    scheduler.scheduleJob(jobDetail, trigger);
                }
            }
        }
    }
    private JobDetail buildJobDetail(EmailData emailData) {
        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("idEmailData", emailData.getId());

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
                .startAt(startAt)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }
}
