package com.github.jpaquerydslmybatis.service;

import com.github.jpaquerydslmybatis.common.converter.mapper.SpringTestMapper;
import com.github.jpaquerydslmybatis.repository.db1.mybatis.TestMybatisDao;
import com.github.jpaquerydslmybatis.repository.db1.jpa.test.SpringTest;
import com.github.jpaquerydslmybatis.repository.db1.jpa.test.SpringTestRepository;
import com.github.jpaquerydslmybatis.web.domain.db1.StudentDto;
import com.github.jpaquerydslmybatis.web.domain.db1.TestDto;
import com.github.jpaquerydslmybatis.web.domain.db1.main.MainImageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestService {
    private final TestMybatisDao testDao;
    private final SpringTestRepository springTestRepository;


    public List<TestDto> getAllData() {
        return testDao.findAll();
    }

    @Transactional(value = "db1TransactionManager", readOnly = true)
    public List<TestDto> getAllData2() {
        List<SpringTest> springTests = springTestRepository.findAll();
        return SpringTestMapper.INSTANCE.toDtoList(springTests);

    }
    @Transactional("db1TransactionManager")
    public void deletedByIDValue(long id) {
        long result = testDao.deleteById(id);
        if(result == 1) return;
        throw new RuntimeException("삭제 실패: ID " + id + "에 해당하는 데이터가 없습니다.");
    }
    @Transactional("db1TransactionManager")
    public long insertAllTest(List<StudentDto> studentDtoList) {
        return testDao.insertAllStudent(studentDtoList);
    }
    @Transactional(value = "db1TransactionManager",readOnly = true)
    public List<StudentDto> getMyStudentListByNames(List<String> studentNames) {
        List<String> myName =
        studentNames==null ?
                List.of("시후1호기", "시후2호기", "시후3호기", "시후4호기", "시후5호기") :
                studentNames;
        return testDao.findByNamesIn(myName);
    }
    @Transactional(value = "db1TransactionManager")
    public List<StudentDto> getMyStudentListByNamesTwo(String[] studentNames) {
        String[] myName =
                studentNames==null ?
                        new String[]{"시후1호기", "시후2호기", "시후3호기", "시후4호기", "시후5호기"} :
                        studentNames;
        return testDao.findByNamesInArr(myName);
    }
    @Transactional("db1TransactionManager")
    public void insertTest(StudentDto studentDto) {
        long result = testDao.insertAllStudent(List.of(studentDto));
        throw new RuntimeException("insertTest 메소드에서 예외 발생: " + result + "건이 삽입되었습니다.");
    }
    @Transactional(value = "db1TransactionManager", readOnly = true)
    public List<MainImageDto> getMainImageList() {
        return testDao.findAllMainImage();
    }
}
