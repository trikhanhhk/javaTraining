package com.javateam.mgep.entity;

import com.javateam.mgep.entity.dto.EmailDataForm;

import javax.persistence.*;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "EmailData")
public class EmailData {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @Column(name = "`id`")
    private Long id;

    @Column(name = "`send_to`")
    private String sendTo;  //gửi đến ai

    @Column(name = "`subject`")
    private String subject;  //Tiêu đề mail

    @Column(name = "`content`")
    private String content;  // Nội dung mail

    @Column(name = "`dept_id`")
    private String deptId;  //Mã phòng ban cần gửi

    @Column(name = "`repeat`")
    private String repeat;  //có định kỳ không value = 1 là định kỳ, khác là không định kỳ

    @Column(name = "type_send")  // gửi cho phòng ban hay cho tất cả
    private String typeSend;

    @Column(name = "`repeat_type`")
    private String repeatType;  //định kỳ theo ngày: 1, tuần: 2, Tháng: 3

    @Column(name = "`status`")
    private String status;   //trạng thái

    @Column(name = "`delete_flg`")
    private String deleteFlg;

    @Column(name = "`start_date`")
    private Date startDate;   //ngày bắt đầu gửi

    @Column(name = "`end_date`")
    private Date endDate;  //ngày kết thúc gửi

    @Column(name = "`time_send`")
    private String timeSend;  //Giờ gửi

    @Column(name = "`create_date`")
    private Date createDate;

    @Column(name = "`date_send`")
    private Date dateSend;

    @Column(name = "date_send_time")
    private Date dateSendTime;

    public EmailData() {
    }

    public EmailData(EmailData emailData) {
        this.id = emailData.getId();

        this.sendTo = emailData.getSendTo();  //gửi đến ai

        this.subject = emailData.getSubject();  //Tiêu đề mail

        this.content = emailData.getContent();  // Nội dung mail

        this.deptId = emailData.getDeptId();  //Mã phòng ban cần gửi

        this.repeat = emailData.getRepeat();  //có định kỳ không value = 1 là định kỳ, khác là không định kỳ

        this.typeSend = emailData.getTypeSend();

        this.repeatType = emailData.getRepeatType();  //định kỳ theo ngày: 1, tuần: 2, Tháng: 3

        this.status = emailData.getStatus();   //trạng thái

        this.deleteFlg = emailData.getDeleteFlg();

        this.startDate = emailData.getCreateDate();   //ngày bắt đầu gửi

        this.endDate = emailData.getEndDate();  //ngày kết thúc gửi

        this.timeSend = emailData.getTimeSend();  //Giờ gửi

        this.createDate = emailData.getCreateDate();

        this.dateSend = emailData.getDateSend();

        this.dateSendTime = emailData.getDateSendTime();
    }

    public EmailData(EmailDataForm emailDataForm) {
        this.subject = emailDataForm.getSubject();  //Tiêu đề mail

        this.content = emailDataForm.getContent();  // Nội dung mail

        this.deptId = emailDataForm.getDeptId();  //Mã phòng ban cần gửi

        this.repeat = emailDataForm.getRepeat();  //có định kỳ không value = 1 là định kỳ, khác là không định kỳ

        // gửi cho phòng ban hay cho tất cả
        this.typeSend = emailDataForm.getTypeSend();

        this.repeatType = emailDataForm.getRepeatType();  //định kỳ theo ngày: 1, tuần: 2, Tháng: 3

        this.timeSend = emailDataForm.getTimeSend();  //Giờ gửi

        this.dateSendTime = emailDataForm.getDateSendTime();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTypeSend() {
        return typeSend;
    }

    public void setTypeSend(String typeSend) {
        this.typeSend = typeSend;
    }

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
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

    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeleteFlg() {
        return deleteFlg;
    }

    public void setDeleteFlg(String deleteFlg) {
        this.deleteFlg = deleteFlg;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getRepeat() {
        return repeat;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getTimeSend() {
        return timeSend;
    }

    public void setTimeSend(String timeSend) {
        this.timeSend = timeSend;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getDateSend() {
        return dateSend;
    }

    public void setDateSend(Date dateSend) {
        this.dateSend = dateSend;
    }
    public Date getDateSendTime() {
        return dateSendTime;
    }

    public void setDateSendTime(Date dateSendTime) {
        this.dateSendTime = dateSendTime;
    }
}
