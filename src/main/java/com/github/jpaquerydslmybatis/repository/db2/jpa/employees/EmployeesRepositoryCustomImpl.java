package com.github.jpaquerydslmybatis.repository.db2.jpa.employees;

import com.github.jpaquerydslmybatis.common.myenum.Gender;
import com.github.jpaquerydslmybatis.repository.db1.jpa.user.QMyUser;
import com.github.jpaquerydslmybatis.repository.db1.jpa.user.emp.QEmpManagement;
import com.github.jpaquerydslmybatis.repository.db2.jpa.department.QDepartments;
import com.github.jpaquerydslmybatis.web.dto.employees.EmpExcelRequest;
import com.github.jpaquerydslmybatis.web.dto.employees.EmpListResponse;
import com.github.jpaquerydslmybatis.web.dto.employees.chart.EmpSearchCondition;
import com.github.jpaquerydslmybatis.web.dto.employees.chart.PieChartResponse;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class EmployeesRepositoryCustomImpl implements EmployeesRepositoryCustom {
    private final @Qualifier("db2QueryFactory") JPAQueryFactory queryFactory;
    private final @Qualifier("db1QueryFactory") JPAQueryFactory db1QueryFactory;

    @Override
    public List<EmpListResponse> findAllEmployeesJoinFetch(List<Long> departmentIds, Gender gender) {
        BooleanExpression departmentFilter = departmentIds == null
                ? null
                : ( // 0 값이 포함되어 있다면, 부서가 없는 직원도 포함
                departmentIds.contains(0L) ?
                        QEmployees.employees.departments.departmentId.in(departmentIds).or(QEmployees.employees.departments.departmentId.isNull())
                        : QEmployees.employees.departments.departmentId.in(departmentIds)
        );
        BooleanExpression genderFilter = enumGenderToBooleanExpression(gender);

        return queryFactory
                .select(Projections.fields(EmpListResponse.class,
                        QEmployees.employees.employeeId,
                        QEmployees.employees.firstName,
                        QEmployees.employees.lastName,
                        QEmployees.employees.departments.departmentId,
                        QEmployees.employees.departments.departmentName,
                        QEmployees.employees.salary,
                        QEmployees.employees.rrn,
                        QEmployees.employees.hireDate,
                        QEmployees.employees.commissionPct
                ))
                .from(QEmployees.employees)
                .leftJoin(QEmployees.employees.departments, QDepartments.departments)
                .where(departmentFilter, genderFilter)
                .orderBy(QEmployees.employees.departments.departmentId.asc().nullsFirst(),
                        QEmployees.employees.employeeId.asc())
                .fetch();
    }

    @Override
    public long insertEmployeeByExcel(EmpExcelRequest request) {
        return queryFactory.insert(QEmployees.employees)
                .columns(
                        QEmployees.employees.employeeId,
                        QEmployees.employees.firstName,
                        QEmployees.employees.lastName,
                        QEmployees.employees.email,
                        QEmployees.employees.phoneNumber,
                        QEmployees.employees.hireDate,
                        QEmployees.employees.jobs.jobId,
                        QEmployees.employees.salary,
                        QEmployees.employees.commissionPct,
                        QEmployees.employees.manager.employeeId,
                        QEmployees.employees.departments.departmentId,
                        QEmployees.employees.rrn
                )
                .values(
                        request.getEmployeeId(),
                        request.getFirstName(),
                        request.getLastName(),
                        request.getEmail(),
                        request.getPhoneNumber(),
                        request.getHireDate(),
                        request.getJobId(),
                        request.getSalary(),
                        request.getCommissionPct(),
                        request.getManagerId(), // managerId가 null일 수 있음
                        request.getDepartmentId(), // departmentId가 null일 수 있음
                        request.getRrn()
                )
                .execute();
    }

    private BooleanExpression enumGenderToBooleanExpression(Gender gender) {
        if (gender == null) return null;
        return switch (gender) {
            case MALE -> QEmployees.employees.rrn.charAt(6).in('1', '3');
            case FEMALE -> QEmployees.employees.rrn.charAt(6).in('2', '4');
            case UNKNOWN -> null;
        };
    }

    @Override
    public List<PieChartResponse> findChartDataByCondition(EmpSearchCondition condition) {
        List<Expression<?>> conditionClause = createExpressionCondition(condition);
        List<Expression<?>> groupCondition = createGroupExpressionCondition(condition);

//        NumberExpression<Long> countExpression = createCountExpression(condition);
        List<OrderSpecifier<?>> orderByCondition = createOrderByCondition(condition);

        JPAQuery<Tuple> selectFromCause = createSelectCause(condition, conditionClause);


        List<Tuple> result = selectFromCause

                .groupBy(groupCondition.toArray(new Expression<?>[0]))
                .orderBy(orderByCondition.toArray(new OrderSpecifier[0]))//배열의 초기 크기는 0 이고 크기가 적절하게 수정된다.
                .fetch();
        long totalCount = result.stream()
                .mapToLong(t -> t.get(Expressions.numberPath(Long.class, "count"))).sum();

        try {
            if (condition == EmpSearchCondition.DEPT_NAME_GENDER)
                return tupleDrillDown(result, totalCount);
            if (condition == EmpSearchCondition.GENDER_HIRE_YEAR)
                return tupleBasicLine(result, totalCount);
            if(condition == EmpSearchCondition.PAGE_URL_USERNAME)
                return tupleBasicBar(result);
            return tupleListToPieChartResponseList(result, totalCount);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<PieChartResponse> tupleBasicBar(List<Tuple> result) {
        return result.stream()
                .map(tuple -> {
                    long count = tuple.get(Expressions.numberPath(Long.class, "count"));
                    String username = tuple.get(Expressions.stringPath("username")) == null ?
                            "사용자 없음" : tuple.get(Expressions.stringPath("username"));
                    String pageUrl = tuple.get(Expressions.stringPath("pageUrl"));
                    String refinedUrl = pageUrl == null ?
                            "페이지 URL 없음" : pageUrl;
                    if(refinedUrl.equals("/emp/chart"))
                        pageUrl = "직원 차트 페이지";
                    else if(refinedUrl.equals("/emp/list"))
                        pageUrl = "직원 목록 페이지";

//                    double percent = totalCount == 0 ? 0 : (count * 100.0 / totalCount);
                    return PieChartResponse.builder()
                            .y(count)
                            .name(username)
                            .deptName(pageUrl)
                            .build();
                })
                .toList();
    }


    private JPAQuery<Tuple> createSelectCause(EmpSearchCondition condition, List<Expression<?>> conditionClause) {
        NumberExpression<Long> countExpression = createCountExpression(condition).as("count");
        Expression<?>[] selectClause = new Expression<?>[conditionClause.size() + 1];
        for (int i = 0; i < conditionClause.size(); i++) {
            selectClause[i] = conditionClause.get(i);
        }
        selectClause[conditionClause.size()] = countExpression;

        return switch (condition) {
            case GENDER, DEPT_NAME, GENDER_HIRE_YEAR, DEPT_NAME_GENDER -> queryFactory.select(
                            selectClause
                    )
                    .from(QEmployees.employees).leftJoin(QEmployees.employees.departments, QDepartments.departments);
            case PAGE_URL_USERNAME -> db1QueryFactory.select(
                            selectClause
                    )
                    .from(QEmpManagement.empManagement).join(QEmpManagement.empManagement.myUser, QMyUser.myUser);
        };


    }

    private NumberExpression<Long> createCountExpression(EmpSearchCondition condition) {
        return switch (condition) {
            case GENDER, DEPT_NAME, GENDER_HIRE_YEAR, DEPT_NAME_GENDER -> QEmployees.employees.count();
            case PAGE_URL_USERNAME -> QMyUser.myUser.count();
        };
    }

    private List<Expression<?>> createExpressionCondition(EmpSearchCondition condition) {
        return switch (condition) {
            case GENDER -> List.of(
                    Expressions.stringTemplate(
                            "case when substr({0},7,1) in ('1','3') then '남자' else '여자' end",
                            QEmployees.employees.rrn
                    ).as("group")
            );

            case DEPT_NAME -> List.of(
                    QDepartments.departments.departmentName.as("group")
            );
            case DEPT_NAME_GENDER -> List.of(
                    QDepartments.departments.departmentName.as("group"),
                    Expressions.stringTemplate(
                            "case when substr({0},7,1) in ('1','3') then '남자' else '여자' end",
                            QEmployees.employees.rrn
                    ).as("genderGroup")
            );
            case PAGE_URL_USERNAME -> List.of(
                    QEmpManagement.empManagement.myUser.username.as("username"),
                    QEmpManagement.empManagement.pageUrl.as("pageUrl")
            );
            case GENDER_HIRE_YEAR -> List.of(
                    Expressions.stringTemplate(
                            "case when substr({0},7,1) in ('1','3') then '남자' else '여자' end",
                            QEmployees.employees.rrn
                    ).as("genderGroup"),
                    QEmployees.employees.hireDate.year().as("yearGroup")
            );
        };
    }

    private List<Expression<?>> createGroupExpressionCondition(EmpSearchCondition condition) {
        return switch (condition) {
            case GENDER -> List.of(
                    Expressions.stringTemplate(
                            "case when substr({0},7,1) in ('1','3') then '남자' else '여자' end",
                            QEmployees.employees.rrn
                    )
            );

            case DEPT_NAME -> List.of(
                    QDepartments.departments.departmentName
            );
            case DEPT_NAME_GENDER -> List.of(
                    QDepartments.departments.departmentName,
                    Expressions.stringTemplate(
                            "case when substr({0},7,1) in ('1','3') then '남자' else '여자' end",
                            QEmployees.employees.rrn
                    )
            );
            case PAGE_URL_USERNAME -> List.of(
                    QEmpManagement.empManagement.myUser.username,
                    QEmpManagement.empManagement.pageUrl
            );

            case GENDER_HIRE_YEAR -> List.of(
                    Expressions.stringTemplate(
                            "case when substr({0},7,1) in ('1','3') then '남자' else '여자' end",
                            QEmployees.employees.rrn
                    ),
                    QEmployees.employees.hireDate.year()
            );
        };
    }


    private List<PieChartResponse> tupleBasicLine(List<Tuple> result, long totalCount) {
        return result.stream()
                .map(tuple -> {
                    long count = tuple.get(Expressions.numberPath(Long.class, "count"));
                    int year = tuple.get(Expressions.numberPath(Integer.class, "yearGroup"));
                    String gender = tuple.get(Expressions.stringPath("genderGroup")) == null ?
                            "성별 없음" : tuple.get(Expressions.stringPath("genderGroup"));
                    double percent = totalCount == 0 ? 0 : (count * 100.0 / totalCount);
                    return PieChartResponse.builder()
                            .y(percent)
                            .name(gender)
                            .year(year)
                            .build();
                })
                .toList();

    }

    private List<PieChartResponse> tupleDrillDown(List<Tuple> result, long totalCount) {
        List<PieChartResponse> responses = new ArrayList<>();
        // 부서별 성별 비율을 구하기 위해 부서명과 성별 그룹으로 그룹화
        // 부서명과 성별 그룹으로 그룹화된 결과를 순회하며 PieChartResponse 객체 생성
        // map을 사용 해서 그룹화 시키기


        Map<String, Long> deptTotalCount = new HashMap<>();
        for (Tuple tuple : result) {
            String name = tuple.get(Expressions.stringPath("group")) == null ?
                    "부서 없음" : tuple.get(Expressions.stringPath("group"));
            long count = tuple.get(Expressions.numberPath(Long.class, "count"));
            deptTotalCount.put(name, deptTotalCount.getOrDefault(name, 0L) + count);
        }

        Map<String, List<PieChartResponse>> deptGroupBy = new HashMap<>();

        for (Tuple tuple : result) {
            String name = tuple.get(Expressions.stringPath("group")) == null ?
                    "부서 없음" : tuple.get(Expressions.stringPath("group"));
            long count = tuple.get(Expressions.numberPath(Long.class, "count"));
            String gender = tuple.get(Expressions.stringPath("genderGroup")) == null ?
                    "성별 없음" : tuple.get(Expressions.stringPath("genderGroup"));


            double percent = totalCount == 0 ? 0 : (count * 100.0 / totalCount);
            long deptTotal = deptTotalCount.getOrDefault(name, 0L);
            double deptGenderPercent = deptTotal == 0 ? 0 : (count * 100.0 / deptTotal);

            PieChartResponse resp = PieChartResponse.builder()
                    .name(gender)
                    .deptName(name)
                    .y(percent)
                    .count(count)
                    .deptGenderPercent(deptGenderPercent)
                    .build();
            if (deptGroupBy.containsKey(name))
                deptGroupBy.get(name).add(resp);
            else deptGroupBy.putIfAbsent(name, new ArrayList<>(List.of(resp)));
            responses.add(resp);
        }
        return responses;


    }


    private List<PieChartResponse> tupleListToPieChartResponseList(List<Tuple> result, long totalCount) throws IllegalAccessException, NoSuchFieldException {
        List<PieChartResponse> responses = new ArrayList<>();
        double maxPercent = 0;
        int maxIdx = -1;
        for (int i = 0; i < result.size(); i++) {
            String name = result.get(i).get(Expressions.stringPath("group")) == null ?
                    "부서 없음" : result.get(i).get(Expressions.stringPath("group"));
            long count = result.get(i).get(Expressions.numberPath(Long.class, "count"));
            double percent = totalCount == 0 ? 0 : (count * 100.0 / totalCount);

            PieChartResponse resp = new PieChartResponse();
            Field nameField = PieChartResponse.class.getDeclaredField("name");
            Field yField = PieChartResponse.class.getDeclaredField("y");
            Field countField = PieChartResponse.class.getDeclaredField("count");
            countField.setAccessible(true);
            nameField.setAccessible(true);
            yField.setAccessible(true);
            countField.set(resp, count);
            nameField.set(resp, name);
            yField.set(resp, percent);

            responses.add(resp);

            if (percent > maxPercent) {
                maxPercent = percent;
                maxIdx = i;
            }
        }

        // 가장 큰 값에 sliced/selected true
        if (maxIdx >= 0) {
            Field slicedField = PieChartResponse.class.getDeclaredField("sliced");
            Field selectedField = PieChartResponse.class.getDeclaredField("selected");
            slicedField.setAccessible(true);
            selectedField.setAccessible(true);
            slicedField.set(responses.get(maxIdx), true);
            selectedField.set(responses.get(maxIdx), true);
        }

        return responses;
    }


    private List<OrderSpecifier<?>> createOrderByCondition(EmpSearchCondition condition) {
        return switch (condition) {
            case DEPT_NAME, GENDER, DEPT_NAME_GENDER -> List.of(QEmployees.employees.count().desc());
            case PAGE_URL_USERNAME -> List.of(QMyUser.myUser.count().desc());
            case GENDER_HIRE_YEAR -> List.of(
                    QEmployees.employees.hireDate.year().asc(),
                    Expressions.stringTemplate(
                            "case when substr({0},7,1) in ('1','3') then '남자' else '여자' end",
                            QEmployees.employees.rrn
                    ).desc()

            );
        };
    }

    private BooleanExpression createWhereByCondition(EmpSearchCondition condition) {
        return switch (condition) {
            case DEPT_NAME, GENDER -> null;
            case DEPT_NAME_GENDER -> QEmployees.employees.jobs.jobId.isNotNull();
            case PAGE_URL_USERNAME -> QEmployees.employees.departments.locations.isNotNull();

            case GENDER_HIRE_YEAR -> null;
        };
    }

}
