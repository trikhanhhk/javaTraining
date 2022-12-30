package com.javateam.mgep.service.impl.service;

import com.javateam.mgep.entity.Authoritty;
import com.javateam.mgep.entity.Department;
import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.entity.dto.EmployeeData;
import com.javateam.mgep.repositories.AuthorityRepository;
import com.javateam.mgep.repositories.DepartmentRepository;
import com.javateam.mgep.repositories.EmployeeRepository;
import com.javateam.mgep.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)

class EmployeeServiceImplTest {


    @InjectMocks
    EmployeeServiceImpl employeeServiceIpml;
    private EmployeeRepository employeeRepository = Mockito.mock(EmployeeRepository.class);
    private DepartmentRepository departmentRepository = Mockito.mock(DepartmentRepository.class);

    private AuthorityRepository authorityRepository = Mockito.mock(AuthorityRepository.class);

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();;

    private EmployeeServiceImpl employeeService = new EmployeeServiceImpl(passwordEncoder,employeeRepository,departmentRepository,authorityRepository);

    @BeforeEach
    void init(){
        Date date1;
        try {
            date1 =new SimpleDateFormat("yyyy-MM-dd").parse("2022-12-28");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Authoritty authoritty = new Authoritty();
        authoritty.setName("ROLE_USER");
        Department department = new Department();
        department.setId(1l);
        department.setName("Java");
        Employee employee = new Employee();
        employee.setId(null);
        employee.setEmail("tvtien34@gmail.com");
        employee.setPasswordHash(passwordEncoder.encode("Tien12345"));
        employee.setAddress("HN");
        employee.setPhoneNumber("0333489895");
        employee.setDateOfBirth(date1);
        employee.setStatus("0");
        employee.setGender("Nam");
        employee.setFirstName("Trần Văn");
        employee.setLastName("Tiến");
        employee.setCreateDate(new Date());
        employee.setUpdateDate(null);
        doReturn(Optional.of(employee)).when(employeeRepository).findById(1L);
        List<Employee> employeeList = Arrays.asList(employee);
        doReturn(employeeList).when(employeeRepository).findAll();
        doReturn(employee).when(employeeRepository).save(employee);
        doReturn(employee).when(employeeRepository).findByEmail("tvtien34@gmail.com");
        doReturn(Optional.of(department)).when(departmentRepository).findById(1l);
        doReturn(Optional.of(authoritty)).when(authorityRepository).findById(authoritty.getName());
    }

    @Test
    void addEmployee() {
        Long id =1L;
        String firstName = "Trần Văn";
        String lastName = "Tiến";
        String dateOfBirth = "2022-12-28";
        String phoneNumber = "0333489895";
        String gender = "Nam";
        String address = "HN";
        String email = "tvtien34@gmail.com";
        String password = "Tien12345";
        String repeatPassword = "Tien12345";
        Long deptId = 1L;
        EmployeeData employeeData = new EmployeeData(id,firstName,lastName,dateOfBirth,phoneNumber,gender,address,email,password,repeatPassword,deptId);
        Date date1;
        try {
             date1 =new SimpleDateFormat("yyyy-MM-dd").parse(employeeData.getDateOfBirth());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Employee employee = new Employee();
        employee.setId(1l);
        employee.setEmail("tvtien34@gmail.com");
        employee.setPasswordHash(passwordEncoder.encode("Tien12345"));
        employee.setAddress("HN");
        employee.setPhoneNumber("0333489895");
        employee.setDateOfBirth(date1);
        employee.setStatus("0");
        employee.setGender("Nam");
        employee.setFirstName("Trần Văn");
        employee.setLastName("Tiến");
        employee.setCreateDate(new Date());
        employee.setDepartment(departmentRepository.findById(1l).get());
        employee.setUpdateDate(null);
        Mockito.when(employeeRepository.save(employee)).thenReturn(employee);
        try {
            Employee employee1 = employeeService.addEmployee(employeeData);
            verify(employeeRepository, times(1)).save(employee);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Test
    void updateEmployee() {
        String address = "HN";
        String phoneNumber = "0333489895";
        String email = "tvtien34@gmail.com";
        Employee employee = employeeRepository.findByEmail(email);
        Mockito.when(employeeRepository.findByEmail(email)).thenReturn(employee);
        Employee employee1 = employeeServiceIpml.updateEmployee(address,phoneNumber,email);
        Assertions.assertNotNull(employee1);
    }
}