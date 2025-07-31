package com.github.jpaquerydslmybatis.common.converter.custom;

import com.github.jpaquerydslmybatis.common.myenum.Gender;
import org.springframework.stereotype.Component;

@Component
public class GenderConverter extends MyConverter<Gender> {
    public GenderConverter() {
        super(Gender.class);
    }
}
