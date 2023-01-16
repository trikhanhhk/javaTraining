package com.javateam.mgep.service;

import com.javateam.mgep.entity.Employee;

public interface ChangePasswordService {
    String changePassword(String oldPassword,String newPassword,String newPassword1);
}
