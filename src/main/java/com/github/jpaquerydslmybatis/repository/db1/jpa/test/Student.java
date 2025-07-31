package com.github.jpaquerydslmybatis.repository.db1.jpa.test;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Student {
    @Id
    private Long studentId;
    private String name;
    private String email;
}
