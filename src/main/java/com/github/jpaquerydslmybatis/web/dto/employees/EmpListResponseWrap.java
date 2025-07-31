package com.github.jpaquerydslmybatis.web.dto.employees;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EmpListResponseWrap {
    private final List<EmpListResponse> empList;
    private final List<Long> departmentIds;
    public static EmpListResponseWrap of(List<EmpListResponse> empList, List<Long> departmentIds) {
        return new EmpListResponseWrap(empList, departmentIds);
    }
//    public static EmpListResponseWrap fromEmpList(List<EmpListResponse> empList) {
//        return new EmpListResponseWrap(empList, null);
//    }

}
