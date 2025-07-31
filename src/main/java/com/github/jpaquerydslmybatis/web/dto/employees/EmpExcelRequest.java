package com.github.jpaquerydslmybatis.web.dto.employees;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor(staticName = "of")
@Builder
public class EmpExcelRequest {
    private final long employeeId;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phoneNumber;
    private final String jobId;
    private final LocalDate hireDate;
    private final Double salary;
    private final Double commissionPct;
    private final Long managerId;
    private final Long departmentId;
    private final String rrn; // 주민등록번호 residentRegistrationNumber
}
