package com.github.jpaquerydslmybatis.repository.db2.jpa.employees;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeesRepository extends JpaRepository<Employees, Long> , EmployeesRepositoryCustom{



}
