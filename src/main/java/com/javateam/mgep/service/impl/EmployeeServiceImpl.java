package com.javateam.mgep.service.impl;

import com.javateam.mgep.constants.AuthoritiesConstants;
import com.javateam.mgep.entity.Authoritty;
import com.javateam.mgep.entity.Department;
import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.entity.dto.EmployeeData;
import com.javateam.mgep.exception.EmailAlreadyUsedException;
import com.javateam.mgep.exception.PasswordNotMatchException;
import com.javateam.mgep.repositories.AuthorityRepository;
import com.javateam.mgep.repositories.DepartmentRepository;
import com.javateam.mgep.repositories.EmployeeRepository;
import com.javateam.mgep.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired DepartmentRepository departmentRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    private final PasswordEncoder passwordEncoder;

    public EmployeeServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Employee addEmployee(EmployeeData employeeData) {
        Optional<Employee> findEmployee = employeeRepository.findOneByEmailIgnoreCase(employeeData.getEmail());
        if(findEmployee.isPresent()) {
            throw new EmailAlreadyUsedException();

        }
        if(!employeeData.getPassword().equals(employeeData.getRepeatPassword())) {
            throw new PasswordNotMatchException();
        }
        Employee newEmployee = new Employee();
        newEmployee.setFirstName(employeeData.getFirstName());
        newEmployee.setLastName(employeeData.getLastName());
        newEmployee.setDateOfBirth(employeeData.getDateOfBirth());
        newEmployee.setPhoneNumber(employeeData.getPhoneNumber());
        newEmployee.setEmail(employeeData.getEmail().toLowerCase());
        Optional<Department> findDept = departmentRepository.findById(employeeData.getDeptId());
        if(findDept.isPresent()) {
            newEmployee.setDepartment(findDept.get());
        }
        newEmployee.setPasswordHash(passwordEncoder.encode(employeeData.getPassword()));
        newEmployee.setGender(employeeData.getGender());
        newEmployee.setStatus("0");
        Set<Authoritty> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newEmployee.setAuthorities(authorities);
        employeeRepository.save(newEmployee);
        return newEmployee;
    }
}
