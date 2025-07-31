package com.github.jpaquerydslmybatis.repository.db1.mybatis;

import com.github.jpaquerydslmybatis.repository.db1.mybatis.mapper.StudentMapper;
import com.github.jpaquerydslmybatis.repository.db1.mybatis.mapper.TestMapper;
import com.github.jpaquerydslmybatis.repository.db1.mybatis.mapper.main.IndexMapper;
import com.github.jpaquerydslmybatis.web.domain.db1.StudentDto;
import com.github.jpaquerydslmybatis.web.domain.db1.TestDto;
import com.github.jpaquerydslmybatis.web.domain.db1.main.MainImageDto;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TestMybatisDao {
// MyBatis를 사용하여 데이터베이스와 상호작용하는 DAO 클래스입니다.
    private final @Qualifier("db1SqlSessionTemplate") SqlSession sqlSession;

    private final TestMapper testMapper;
    private final StudentMapper studentMapper;
    private final IndexMapper indexMapper;


    public List<TestDto> findAll(){
        // MyBatis를 사용하여 데이터베이스에서 모든 TestDto 객체를 조회하는 로직을 구현합니다.
        // 예시로, MyBatis Mapper를 호출하여 데이터를 가져오는 코드를 작성할 수 있습니다.
        // 아래는 가상의 예시입니다.

         return sqlSession.selectList("begin.selectMemberAll");
    }

    public long deleteById(long id) {
        return testMapper.deleteById(id);
    }

    public long insertAllStudent(List<StudentDto> studentDtoList) {
        return studentMapper.insertAllStudent(studentDtoList);
    }

    public List<StudentDto> findByNamesIn(List<String> myName) {
        return studentMapper.findByNamesIn(myName);
    }

    public List<StudentDto> findByNamesInArr(String[] myName) {
        return studentMapper.findByNamesInTwo(myName);
    }

    public List<MainImageDto> findAllMainImage() {
        return indexMapper.findAllMainImage();
    }
}
