package com.javateam.mgep.service;

import com.javateam.mgep.entity.Employee;

public interface ConfirmationTokenService {
    Employee getEmployeeByToken(String tokenId);
}
