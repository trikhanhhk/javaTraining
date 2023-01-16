package com.javateam.mgep.entity.dto;

import javax.xml.crypto.Data;
import java.util.Date;

public class EmailDataForm {

    private String subject;  //Tiêu đề mail

    private String content;  // Nội dung mail

    private String deptId;  //Mã phòng ban cần gửi

    private String repeat;  //có định kỳ không value = 1 là định kỳ, khác là không định kỳ

    // gửi cho phòng ban hay cho tất cả
    private String typeSend;

    private String repeatType;  //định kỳ theo ngày: 1, tuần: 2, Tháng: 3

    private String startDate;   //ngày bắt đầu gửi

    private String endDate;  //ngày kết thúc gửi

    private String timeSend;  //Giờ gửi

    private Date dateSendTime;

    public EmailDataForm() {
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getTypeSend() {
        return typeSend;
    }

    public void setTypeSend(String typeSend) {
        this.typeSend = typeSend;
    }

    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getTimeSend() {
        return timeSend;
    }

    public void setTimeSend(String timeSend) {
        this.timeSend = timeSend;
    }

    public Date getDateSendTime() {
        return dateSendTime;
    }

    public void setDateSendTime(Date dateSendTime) {
        this.dateSendTime = dateSendTime;
    }
}
