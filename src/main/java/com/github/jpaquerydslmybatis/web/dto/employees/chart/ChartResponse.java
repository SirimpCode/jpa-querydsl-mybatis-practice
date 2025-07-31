package com.github.jpaquerydslmybatis.web.dto.employees.chart;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(staticName = "of")
public class ChartResponse<T> {
    private List<T> data;
    private String label;
    private String thead;
}
