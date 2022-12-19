package com.javateam.mgep.service;

import com.javateam.mgep.entity.Employee;

public interface ChangePasswordService {
    Employee changePassword(String oldPassword,String newPassword,String newPassword1);
}
