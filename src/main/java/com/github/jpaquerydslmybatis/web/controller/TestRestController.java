package com.github.jpaquerydslmybatis.web.controller;

import com.github.jpaquerydslmybatis.service.TestService;
import com.github.jpaquerydslmybatis.web.domain.db1.StudentDto;
import com.github.jpaquerydslmybatis.web.domain.db1.TestDto;
import com.github.jpaquerydslmybatis.web.domain.db1.main.MainImageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestRestController {
    private final TestService testService;

    @GetMapping("/hello")
    public List<TestDto> hello() {
        return testService.getAllData();
    }
    @GetMapping("/hello2")
    public List<MainImageDto> hello2() {
        return testService.getMainImageList();
    }
    @PostMapping("/insert")
    public String insert(@RequestBody StudentDto studentDto) {
        testService.insertTest(studentDto);
        return "데이터가 삽입되었습니다.";
    }

    @DeleteMapping
    public String delete(@RequestParam long id) {
        // 삭제 로직을 구현합니다.
        testService.deletedByIDValue(id);
        return "삭제되었습니다.";
    }

    //리스트 타입 데이터 디비에넣기
    @PostMapping("/third")
    public String insertStudent() {
        List<StudentDto> studentDtoList = new ArrayList<>();
        for (int i = 0; i < 5 ; i++) {
            StudentDto studentDto = new StudentDto();
            studentDto.setName("시후" + (i+1) + "호기");
            studentDto.setStudentId("2025-" + (i + 1));
            studentDto.setEmail("temp" + (i+1) + "@gmail.com");
            studentDtoList.add(studentDto);
        }
        long result = testService.insertAllTest(studentDtoList);
        return "학생 데이터가 " + result + "개 삽입되었습니다.";
    }
    //sql 구문의 where 절에 in을 사용하기
    @GetMapping("/in")
    public List<StudentDto> usedInQuery(@RequestParam List<String> studentNames) {
        return testService.getMyStudentListByNames(studentNames);

    }
}
