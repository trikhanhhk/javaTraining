package com.javateam.mgep.entity;

import javax.persistence.*;

@Entity
@Table(name = "authority")
public class Authoritty {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    private Long id;

    @Column(name = "name")
    private String name;
}
