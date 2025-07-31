package com.github.jpaquerydslmybatis.repository.db2.jpa.job;

import com.github.jpaquerydslmybatis.repository.db2.jpa.department.Departments;
import com.github.jpaquerydslmybatis.repository.db2.jpa.employees.Employees;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_history")
public class JobHistory {
    @EmbeddedId
    private JobHistoryPk jobHistoryPk;
    private LocalDate endDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Jobs jobs;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Departments departments;

    private static JobHistory onlyId(Employees employees, LocalDate startDate){
        JobHistory jobHistory = new JobHistory();
        jobHistory.jobHistoryPk = JobHistoryPk.of(employees, startDate);
        return jobHistory;
    }
}
