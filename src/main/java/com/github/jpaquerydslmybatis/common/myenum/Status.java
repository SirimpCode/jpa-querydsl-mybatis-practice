package com.github.jpaquerydslmybatis.common.myenum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status implements MyEnumInterface{
    ACTIVE("정상"),
    DELETED("삭제"),
    ETC("기타");

    private final String value;
}
