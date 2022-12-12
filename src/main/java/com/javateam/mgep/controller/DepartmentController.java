//package com.javateam.mgep.controller;
//
//import com.javateam.mgep.entity.Department;
//import com.javateam.mgep.service.DepartmentService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//
//import java.util.List;
//
//@Controller
//public class DepartmentController {
//    @Autowired
//    DepartmentService departmentService;
//    @GetMapping("/department")
//    public List<Department> getListDepartment () {
//        List<Department> listDept = departmentService.getListDept();
//        return listDept;
//    }
//}
