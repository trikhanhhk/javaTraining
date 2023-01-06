package com.javateam.mgep.service.impl.service;

import com.javateam.mgep.entity.*;
import com.javateam.mgep.entity.dto.EmployeeData;
import com.javateam.mgep.repositories.AuthorityRepository;
import com.javateam.mgep.repositories.ConfirmationTokenRepository;
import com.javateam.mgep.repositories.DepartmentRepository;
import com.javateam.mgep.repositories.EmployeeRepository;
import com.javateam.mgep.service.impl.ChangePasswordServiceIpml;
import com.javateam.mgep.service.impl.ConfirmationTokenServiceIpml;
import com.javateam.mgep.service.impl.DepartmentService;
import com.javateam.mgep.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private Authentication authentication = Mockito.mock(Authentication.class);
    private SecurityContext securityContext = Mockito.mock(SecurityContext.class);

    private ConfirmationTokenRepository confirmationTokenRepository = Mockito.mock(ConfirmationTokenRepository.class);

    private EmployeeServiceImpl employeeService = new EmployeeServiceImpl(passwordEncoder, employeeRepository, departmentRepository, authorityRepository);

    private ConfirmationTokenServiceIpml confirmationTokenServiceIpml = new ConfirmationTokenServiceIpml(confirmationTokenRepository,employeeRepository);

    private DepartmentService departmentServices = new DepartmentService(departmentRepository);

    private ChangePasswordServiceIpml changePasswordServiceIpml = new ChangePasswordServiceIpml(passwordEncoder, employeeRepository);

    private
    @BeforeEach
    void init() {
        Date date1;
        try {
            date1 = new SimpleDateFormat("yyyy-MM-dd").parse("2022-12-28");
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

        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setTokenid(1);
        confirmationToken.setConfirmationToken("1abc");
        confirmationToken.setCreatedDate(new Date());
        confirmationToken.setUserEntity(employee);



        List<Employee> employeeList = Arrays.asList(employee);

        List<Department> departmentList = Arrays.asList(department);
        doReturn(Optional.of(employee)).when(employeeRepository).findById(1L);
        doReturn(employeeList).when(employeeRepository).findAll();
        doReturn(employee).when(employeeRepository).save(employee);
        doReturn(employee).when(employeeRepository).findByEmail("tvtien34@gmail.com");
        doReturn(Optional.of(department)).when(departmentRepository).findById(1l);
        doReturn(Optional.of(department)).when(departmentRepository).findById(1l);
        doReturn(departmentList).when(departmentRepository).findAll();
        doReturn(confirmationToken).when(confirmationTokenRepository).findByConfirmationToken("1abc");
        doReturn(Optional.of(employee)).when(employeeRepository).findOneByEmailIgnoreCase("tvtien34@gmail.com");



//        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
//        SecurityContextHolder.setContext(securityContext);
//        GrantedAuthority authority = new SimpleGrantedAuthority(authoritty.getName());
//        List<GrantedAuthority> authorities = Arrays.asList(authority);
//        CustomUserDetails customUserDetails = new CustomUserDetails(employee, authorities);
//        doReturn(customUserDetails).when(securityContext.getAuthentication().getPrincipal());
    }

    @Test
    void addEmployee() {
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
        EmployeeData employeeData = new EmployeeData(firstName, lastName, dateOfBirth, phoneNumber, gender, address, email, password, repeatPassword, deptId);
        Employee employee = null;
        try {
            employee = employeeService.addEmployee(employeeData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assertions.assertNotNull(employee);


    }

    @Test
    void updateEmployee() {
        String address = "HN";
        String phoneNumber = "0333489895";
        String email = "tvtien34@gmail.com";
        Employee employee = employeeRepository.findByEmail(email);
        Mockito.when(employeeRepository.findByEmail(email)).thenReturn(employee);
        Employee employee1 = employeeServiceIpml.updateEmployee(address, phoneNumber, email);
        Assertions.assertNotNull(employee1);
    }

    @Test
    @DisplayName("findAll can return a list has 1 department")
    void whenfindAll_thenReturnListHasOneElement() {
        List<Department> departmentList = departmentServices.getListDept();
        Assertions.assertTrue(departmentList.size() == 1, "findAll can return a list has 1 department successful");
    }

    @Test
    @DisplayName("changePassword successful")
    void whenChangePasswordSuccessful() {
        String oldPassword = "Tien12345";
        String beforeNewPassword = "Khanh321";
        String afterNewPassword = "Khanh321";
        Employee employee = changePasswordServiceIpml.changePassword(oldPassword, beforeNewPassword, afterNewPassword);
        Assertions.assertNotNull(employee, "changePassword successful");
    }

    @Test
    @DisplayName("changePassword successful")
    void whenGetEmployeeByTokenSuccessful() {
         Employee employee = confirmationTokenServiceIpml.getEmployeeByToken("1abc");
         Assertions.assertNotNull(employee, "changePassword successful");
    }
}
