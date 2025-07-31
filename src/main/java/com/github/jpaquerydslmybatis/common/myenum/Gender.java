package com.github.jpaquerydslmybatis.common.myenum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public enum Gender implements MyEnumInterface {
    FEMALE("여자"),
    MALE("남자"),
    UNKNOWN("반반섞임");
    private final String value;

}
