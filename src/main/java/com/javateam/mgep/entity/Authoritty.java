package com.javateam.mgep.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "authority")
public class Authoritty {

    @Id
    @Column(name = "name")
    private String name;



    public Authoritty() {
    }

    public Authoritty(String name) {
        this.name = name;
//        this.employees = employees;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public Set<Employee> getEmployees() {
//        return employees;
//    }
//
//    public void setEmployees(Set<Employee> employees) {
//        this.employees = employees;
//    }

    @Override
    public String toString() {
        return "Authoritty{" +
                "name='" + name + '\'' +
                '}';
    }
}
