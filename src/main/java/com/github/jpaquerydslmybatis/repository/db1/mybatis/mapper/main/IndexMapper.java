package com.github.jpaquerydslmybatis.repository.db1.mybatis.mapper.main;

import com.github.jpaquerydslmybatis.web.domain.db1.main.MainImageDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IndexMapper {
    List<MainImageDto> findAllMainImage();
}
