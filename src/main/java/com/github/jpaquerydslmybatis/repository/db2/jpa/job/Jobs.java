package com.github.jpaquerydslmybatis.repository.db2.jpa.job;

import com.github.jpaquerydslmybatis.repository.db2.jpa.employees.Employees;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;

import java.util.List;

@Entity
@Getter
public class Jobs {
    @Id
    private String jobId;
    private String jobTitle;
    private long minSalary;
    private long maxSalary;

    @OneToMany(mappedBy = "jobs")
    private List<Employees> employees;


}
