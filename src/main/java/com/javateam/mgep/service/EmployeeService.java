package com.javateam.mgep.service;


import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.entity.dto.EmployeeData;
import com.javateam.mgep.entity.dto.SearchCriteria;
import org.quartz.SchedulerException;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.List;

public interface EmployeeService {
    public Employee addEmployee(EmployeeData employeeData, boolean importExFlg) throws ParseException, SchedulerException;
    public Employee updateEmployee(String address, String phone,String email);

    public Employee updateEmployeeAdmin(EmployeeData employeeData,String role);
    public List<Employee> getListAll();

    public List<Employee> searchByData(SearchCriteria searchCriteria);

    public List<Employee> importFileEx(MultipartFile file) throws Exception;

    public String deleteEmployeeById(Long id);
}
