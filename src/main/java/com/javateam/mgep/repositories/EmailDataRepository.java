package com.javateam.mgep.repositories;

import com.javateam.mgep.entity.EmailData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmailDataRepository extends JpaRepository<EmailData, Long> {
    @Query("select distinct emailData from EmailData emailData where emailData.repeat = '1' and emailData.endDate < current_date or emailData.endDate = current_date")
    List<EmailData> findEmailDataByRepeat();
}
