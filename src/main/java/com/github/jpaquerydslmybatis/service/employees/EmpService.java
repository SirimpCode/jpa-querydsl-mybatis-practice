package com.github.jpaquerydslmybatis.service.employees;

import com.github.jpaquerydslmybatis.common.myenum.Gender;
import com.github.jpaquerydslmybatis.repository.db1.jpa.user.MyUser;
import com.github.jpaquerydslmybatis.repository.db1.jpa.user.emp.EmpManagement;
import com.github.jpaquerydslmybatis.repository.db1.jpa.user.emp.EmpManagementRepository;
import com.github.jpaquerydslmybatis.repository.db2.jpa.employees.Employees;
import com.github.jpaquerydslmybatis.repository.db2.jpa.employees.EmployeesRepository;
import com.github.jpaquerydslmybatis.web.dto.employees.EmpListResponse;
import com.github.jpaquerydslmybatis.web.dto.employees.EmpListResponseWrap;
import com.github.jpaquerydslmybatis.web.dto.employees.chart.ChartResponse;
import com.github.jpaquerydslmybatis.web.dto.employees.chart.EmpSearchCondition;
import com.github.jpaquerydslmybatis.web.dto.employees.chart.PieChartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpService {
    private final EmployeesRepository empRepo;
    private final EmpManagementRepository empManagementRepo;

    @Transactional(readOnly = true, value = "db2TransactionManager")
    public EmpListResponseWrap getAllEmployees(List<Long> departmentIds, Gender gender) {
        List<EmpListResponse> result = empRepo.findAllEmployeesJoinFetch(departmentIds, gender);
        //중복은 제거
        List<Long> deptIds = result.stream().map(EmpListResponse::getDepartmentId)
                .sorted(Comparator.nullsFirst(Comparator.naturalOrder()))
                .distinct().toList();
        return EmpListResponseWrap.of(result, deptIds);
    }

    @Transactional(readOnly = true, value = "db2TransactionManager")
    public ChartResponse<PieChartResponse> getPieChartData(EmpSearchCondition condition) {
        List<PieChartResponse> result = empRepo.findChartDataByCondition(condition);
        // PieChartResponse 리스트 내림차순 정렬 orderBy 없을시 아래 로직사용
//        result.sort(Comparator.comparingDouble(PieChartResponse::getY).reversed());
        return ChartResponse.of(result, condition.getDescription(), condition.getTHead());
    }

    @Transactional("db1TransactionManager")
    public void createEmpManagementLog(String userId, String userIp, String currentURL) {
        MyUser loginUser = MyUser.onlyId(userId);
        EmpManagement empManagement = EmpManagement.of(loginUser, userIp, currentURL);
        empManagementRepo.save(empManagement);
    }
}
