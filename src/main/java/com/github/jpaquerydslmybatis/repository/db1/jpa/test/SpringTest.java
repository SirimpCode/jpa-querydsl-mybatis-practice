package com.github.jpaquerydslmybatis.repository.db1.jpa.test;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.LocalDateTime;

@Table(name = "spring_test")
@Entity
@Getter
public class SpringTest {
    @Id
    private long no;
    private String name;
    @Column(name = "writeday")
    private LocalDateTime createdAt;
}
