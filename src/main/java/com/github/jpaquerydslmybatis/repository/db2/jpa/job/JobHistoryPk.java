package com.github.jpaquerydslmybatis.repository.db2.jpa.job;


import com.github.jpaquerydslmybatis.repository.db2.jpa.employees.Employees;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@EqualsAndHashCode
@Getter
@Embeddable
@AllArgsConstructor(staticName = "of", access = AccessLevel.PACKAGE)
@NoArgsConstructor
public class JobHistoryPk {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employees employees;
    private LocalDate startDate;
}
