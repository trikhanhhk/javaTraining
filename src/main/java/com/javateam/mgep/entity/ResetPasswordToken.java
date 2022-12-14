package com.javateam.mgep.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "reset_password_token")
public class ResetPasswordToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="token_id")
    private long tkenId;

    @Column(name="reset_password_token")
    private String resetPasswordToken;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @OneToOne(targetEntity = Employee.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private Employee userEntity;

    public ResetPasswordToken() {
    }

    public ResetPasswordToken(Employee userEntity) {
        this.userEntity = userEntity;
        createdDate = new Date();
        resetPasswordToken = UUID.randomUUID().toString();
    }

    public long gettkenId() {
        return tkenId;
    }

    public void settkenId(long tkenId) {
        this.tkenId = tkenId;
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Employee getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(Employee userEntity) {
        this.userEntity = userEntity;
    }
}
