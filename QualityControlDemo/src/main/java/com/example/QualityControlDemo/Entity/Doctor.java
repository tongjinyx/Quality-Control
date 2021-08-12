package com.example.QualityControlDemo.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String name;
    public String type;
    public Integer gender;

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public int getGender() {
        return gender;
    }

    public String getName() {
        return name;
    }
}
