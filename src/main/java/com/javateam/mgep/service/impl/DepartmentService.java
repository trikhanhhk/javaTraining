package com.javateam.mgep.service.impl;

import com.javateam.mgep.entity.Department;
import com.javateam.mgep.repositories.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService implements com.javateam.mgep.service.DepartmentService {
    @Autowired
    DepartmentRepository departmentRepository;

    @Override
    public List<Department> getListDept() {
        return departmentRepository.findAll();
    }
}
