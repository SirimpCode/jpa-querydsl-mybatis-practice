package com.github.jpaquerydslmybatis.repository.db2.jpa.department;

import com.github.jpaquerydslmybatis.repository.db2.jpa.employees.Employees;
import com.github.jpaquerydslmybatis.repository.db2.jpa.locations.Locations;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;
@Getter
@Entity
public class Departments {
    @Id
    private Long departmentId;
    private String departmentName;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employees manager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Locations locations;

    @OneToMany(mappedBy = "departments")
    private List<Employees> employeesList;

}
