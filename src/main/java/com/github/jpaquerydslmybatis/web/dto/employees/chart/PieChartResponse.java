package com.github.jpaquerydslmybatis.web.dto.employees.chart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PieChartResponse {

    private String name;
    private String deptName;
    private double y;
    private boolean sliced;
    private boolean selected;
    private long count;
    private double deptGenderPercent;
    private long year;



}
