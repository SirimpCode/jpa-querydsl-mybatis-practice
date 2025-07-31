package com.github.jpaquerydslmybatis.common.converter.mapper;

import com.github.jpaquerydslmybatis.repository.db1.jpa.test.SpringTest;
import com.github.jpaquerydslmybatis.web.domain.db1.TestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SpringTestMapper {
    SpringTestMapper INSTANCE = Mappers.getMapper(SpringTestMapper.class);
    @Mapping(source = "createdAt", target = "writeday")
    @Mapping(source = "no", target = "no")
    TestDto toDto(SpringTest entity);

     List<TestDto> toDtoList(List<SpringTest> entities);
}
