package com.github.jpaquerydslmybatis.web.dto.employees.chart;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.github.jpaquerydslmybatis.common.exception.CustomBadRequestException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmpSearchCondition {
    DEPT_NAME("부서 별 조회", "부서명"),
    GENDER("성별 별 조회", "성별"),
    GENDER_HIRE_YEAR("입사년도 및 성별 조회", "입사년도, 성별"),
    DEPT_NAME_GENDER("부서 별 성별 조회", "부서명, 성별"),
    PAGE_URL_USERNAME("페이지 URL 별 사용자 조회", "페이지 URL, 사용자명")
    ;
    private final String description;
    private final String tHead;

    //@RequestParam 으로 매핑될때 사용될 메서드
    public static EmpSearchCondition from(String value) {
        for (EmpSearchCondition condition : EmpSearchCondition.values()) {
            if (condition.name().replace("_", "").equalsIgnoreCase(value))
                return condition;
        }
        throw CustomBadRequestException.of().customMessage("조회 조건이 잘못 되었습니다.")
                .request(value).build();
    }

}
