package com.javateam.mgep.service;


import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.entity.dto.EmployeeData;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.List;

public interface EmployeeService {
    public Employee addEmployee(EmployeeData employeeData) throws ParseException;
    public Employee updateEmployee(String address, String phone,String email);

    public Employee updateEmployeeAdmin(EmployeeData employeeData);
    public List<Employee> getListAll();

    public String importFileEx(MultipartFile file);
}
