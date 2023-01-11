package com.javateam.mgep.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@Table(name = "EmailData")
@NoArgsConstructor
public class EmailData {
    @Id
    private Long id;

    @Column(name = "type_send")
    private String typeSend;

    @Column(name = "send_to")
    private String sendTo;

    @Column(name = "title")
    private String title;

    @Column(name = "subject")
    private String subject;

    @Column(name = "content")
    private String content;

    @Column(name = "type")
    private String type;

    @Column(name = "repeat_type")
    private String repeatType;

    @Column(name = "status")
    private String status;

    @Column (name = "date_start")
    private Date dateStart;

    @Column (name = "date_end")
    private Date dateEnd;

    @Column(name = "delete_flg")
    private String deleteFlg;

}
