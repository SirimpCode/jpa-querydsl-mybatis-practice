package com.github.jpaquerydslmybatis.common.converter.custom;

import com.github.jpaquerydslmybatis.common.myenum.RoleEnum;
import org.springframework.stereotype.Component;

@Component
public class RoleEnumConverter extends  MyConverter<RoleEnum> {
    public RoleEnumConverter() {
        super(RoleEnum.class);
    }
}
