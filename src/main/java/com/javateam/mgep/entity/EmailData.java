package com.javateam.mgep.entity;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;

@Entity
@Table(name = "EmailData")
public class EmailData {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    private Long id;

    @Column(name = "send_to")
    private String sendTo;  //gửi đến ai

    @Column(name = "subject")
    private String subject;  //Tiêu đề mail

    @Column(name = "content")
    private String content;  // Nội dung mail

    @Column(name = "dept_id")
    private String deptId;  //Mã phòng ban cần gửi

    @Column(name = "repeat")
    private String repeat;  //có định kỳ không value = 1 là định kỳ, khác là không định kỳ

    @Column(name = "type_send")  // gửi cho phòng ban hay cho tất cả
    private String typeSend;

    @Column(name = "repeat_type")
    private String repeatType;  //định kỳ theo ngày: 1, tuần: 2, Tháng: 3

    @Column(name = "status")
    private String status;   //trạng thái

    @Column(name = "delete_flg")
    private String deleteFlg;

    @Column(name = "start_date")
    private Date startDate;   //ngày bắt đầu gửi

    @Column(name = "end_date")
    private Date endDate;  //ngày kết thúc gửi

    @Column(name = "time_send")
    private Time timeSend;  //Giờ gửi

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "date_send")
    private Date dateSend;

    public EmailData() {
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

    public Time getTimeSend() {
        return timeSend;
    }

    public void setTimeSend(Time timeSend) {
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
}
