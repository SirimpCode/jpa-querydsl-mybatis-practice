package com.github.jpaquerydslmybatis.common.converter.custom;

import com.github.jpaquerydslmybatis.common.myenum.RoleEnum;
import com.github.jpaquerydslmybatis.common.myenum.Status;
import org.springframework.stereotype.Component;

@Component
public class StatusConverter extends MyConverter<Status>{
        public StatusConverter() {
            super(Status.class);
        }

}
