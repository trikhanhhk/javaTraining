package com.javateam.mgep.repositories;

import com.javateam.mgep.entity.EmailData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmailDataRepository extends JpaRepository<EmailData, Long> {
    //Lấy ra những email có ngày gửi trùng với ngày hôm nay và là mail định kỳ và là mail chưa được gửi.
    @Query("select distinct emailData from EmailData emailData where emailData.repeat = '1' and emailData.status ='0'")
    List<EmailData> findEmailDataByRepeat();
}
