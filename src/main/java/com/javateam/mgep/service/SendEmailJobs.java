package com.javateam.mgep.service;

import com.javateam.mgep.entity.EmailData;
import com.javateam.mgep.repositories.EmailDataRepository;
import com.javateam.mgep.service.impl.SendMailServiceIpml;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Time;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SendEmailJobs implements Job {
    private EmailData emailData;

    @Autowired
    EmailDataRepository emailDataRepository;

    @Autowired
    SendMailServiceIpml sendMailServiceIpml;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Date currentTime = new Date();
        LocalDate currentLocalTime = LocalDate.from(currentTime.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime());
        Time current = new Time(currentTime.getTime());
        List<EmailData> emailDataList = emailDataRepository.findEmailDataByRepeat();
        if (emailDataList != null) {
            for (EmailData e : emailDataList) {
                if ("1".equals(e.getRepeatType())) {
                    if (e.getTimeSend().getHours() == current.getHours() && e.getTimeSend().getMinutes() == current.getMinutes()) {
                        if("3".equals(e.getTypeSend())) {
//                            sendMailServiceIpml.sendEmailToAll(e);
                        } else if ("1".equals(e.getTypeSend())) {
//                            sendMailServiceIpml.sendEmailToGroup(e);
                        }
                    }
                } else if ("2".equals(e.getRepeatType())) {
                    Date startDateWeek = e.getStartDate();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(startDateWeek);
                    if (e.getTimeSend().getHours() == current.getHours() && e.getTimeSend().getMinutes() == current.getMinutes()) {
                        while (true) {
//                            Date dateSend = new Date(e.getStartDate().plusDays(7).toString());
//                            if(dateSend.equals())
                        }
                    }
                } else if ("3".equals(e.getRepeatType())) {

                }
            }

        }
    }
}
