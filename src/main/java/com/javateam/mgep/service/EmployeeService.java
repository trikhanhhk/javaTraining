package com.javateam.mgep.service;


import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.entity.dto.EmployeeData;

public interface EmployeeService {
    public Employee addEmployee(EmployeeData employeeData);
    public Employee updateEmployee(String address, String phone,String email);
}
