package com.github.jpaquerydslmybatis.repository.db2.jpa.employees;

import com.github.jpaquerydslmybatis.common.myenum.Gender;
import com.github.jpaquerydslmybatis.web.dto.employees.EmpExcelRequest;
import com.github.jpaquerydslmybatis.web.dto.employees.EmpListResponse;
import com.github.jpaquerydslmybatis.web.dto.employees.chart.EmpSearchCondition;
import com.github.jpaquerydslmybatis.web.dto.employees.chart.PieChartResponse;

import java.util.List;

public interface EmployeesRepositoryCustom {
    List<EmpListResponse> findAllEmployeesJoinFetch(List<Long> departmentIds, Gender gender);

    long insertEmployeeByExcel(EmpExcelRequest request);

    List<PieChartResponse> findChartDataByCondition(EmpSearchCondition condition);

}
