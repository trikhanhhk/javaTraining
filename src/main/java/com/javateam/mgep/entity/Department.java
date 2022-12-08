package com.javateam.mgep.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Table(name = "Department")
@Entity
public class Department {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "department")
    private Set<Employee> employees = new HashSet<>();

    public Department() {

    }

    public Department(Long id) {
        this.id = id;
    }
}
