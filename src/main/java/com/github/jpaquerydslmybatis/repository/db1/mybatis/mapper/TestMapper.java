package com.github.jpaquerydslmybatis.repository.db1.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TestMapper {
    int deleteById(@Param("id") long id);


}
