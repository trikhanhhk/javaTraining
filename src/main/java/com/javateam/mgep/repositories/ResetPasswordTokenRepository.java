package com.javateam.mgep.repositories;

import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.entity.ResetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, Long> {
    public ResetPasswordToken findByResetPasswordToken(String token);
    public List<ResetPasswordToken> findByUserEntity(Employee employee);
}
