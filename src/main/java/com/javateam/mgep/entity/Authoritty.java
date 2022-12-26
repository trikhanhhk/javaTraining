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
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Authoritty{" +
                "name='" + name + '\'' +
                '}';
    }
}
