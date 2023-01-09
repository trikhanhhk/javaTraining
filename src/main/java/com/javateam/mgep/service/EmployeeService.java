package com.javateam.mgep.service;


import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.entity.dto.EmployeeData;
import com.javateam.mgep.entity.dto.SearchCriteria;

import java.text.ParseException;
import java.util.List;

public interface EmployeeService {
    public Employee addEmployee(EmployeeData employeeData) throws ParseException;
    public Employee updateEmployee(String address, String phone,String email);

    public Employee updateEmployeeAdmin(EmployeeData employeeData);
    public List<Employee> getListAll();

    public List<Employee> searchByData(SearchCriteria searchCriteria);
}
