package com.github.jpaquerydslmybatis.repository.db1.mybatis.mapper;

import com.github.jpaquerydslmybatis.web.domain.db1.StudentDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentMapper {
//    int insertStudent(StudentDto student);

    long insertAllStudent(@Param("list") List<StudentDto> studentDtoList);

    List<StudentDto> findByNamesIn(@Param("list") List<String> myName);

    List<StudentDto> findByNamesInTwo(@Param("array") String[] myName);
}
