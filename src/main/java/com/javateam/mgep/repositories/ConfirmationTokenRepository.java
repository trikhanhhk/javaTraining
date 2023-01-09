package com.javateam.mgep.repositories;

import com.javateam.mgep.entity.ConfirmationToken;
import com.javateam.mgep.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    ConfirmationToken findByConfirmationToken(String confirmationToken);
    @Query("SELECT u FROM ConfirmationToken u WHERE u.userEntity.id = ?1")
    ConfirmationToken findByUserEntity(Long id);
}
