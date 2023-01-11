package com.javateam.mgep.repositories;

import com.javateam.mgep.entity.EmailData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SendMailRepository extends JpaRepository<EmailData, Long> {
}
