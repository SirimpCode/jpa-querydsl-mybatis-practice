package com.github.jpaquerydslmybatis.web.controller;

import com.github.jpaquerydslmybatis.service.TestService;
import com.github.jpaquerydslmybatis.web.domain.db1.StudentDto;
import com.github.jpaquerydslmybatis.web.domain.db1.TestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestViewController {
    private final TestService testService;

    @GetMapping("/first")
    public String first() {
        return "test";
    }

    // 뷰단을 나타낼때 return 타입을 String 을 사용하지 않고
    // ModelAndView 를 사용하면 더 많은 기능을 사용할 수 있습니다.
    @GetMapping("/second")
    public ModelAndView modelAndViewTest(ModelAndView modelAndView) {
        List<TestDto> testData = testService.getAllData2();
        modelAndView.setViewName("test");
        modelAndView.addObject("testData", testData);
        //request.setAttribute("testData", testData);
        //model.addAttribute("testData", testData); 랑 같다
        return modelAndView;
    }
    //리스트 타입 데이터 디비에넣기 value와 path 속성은 동일한 역할을 합니다.
//    @GetMapping(value = "/third", produces = "text/plain;charset=UTF-8")
//    @ResponseBody//한글이 깨지지 않도록 하기 위해서 produces 속성을 사용합니다.
//    public String insertStudent() {
//        List<StudentDto> studentDtoList = new ArrayList<>();
//        for (int i = 0; i < 5 ; i++) {
//            StudentDto studentDto = new StudentDto();
//            studentDto.setName("시후" + i+1 + "호기");
//            studentDto.setStudentId(2025 + i + 1L);
//            studentDto.setEmail("temp" + i+1 + "@gmail.com");
//            studentDtoList.add(studentDto);
//        }
//        long result = testService.insertAllTest(studentDtoList);
//        return "";
//    }
    @GetMapping("/student/in")
    public String selectByIn(Model model,
                             @RequestParam(required = false, value = "name") List<String> studentNames) {
        List<StudentDto> responses = testService.getMyStudentListByNames(studentNames);
        model.addAttribute("studentList", responses);
        //배열을 써복
        List<StudentDto> responses2 = testService.getMyStudentListByNamesTwo(studentNames.toArray(new String[0]));
        model.addAttribute("studentList2", responses2);
        return "student/student_list";
    }
//    @GetMapping("/student/in2")
//    public String selectByIn2(Model model,
//                             @RequestParam(required = false, value = "name") String[] studentNames) {
//        List<StudentDto> responses = testService.getMyStudentListByNamesTwo(studentNames);
//        model.addAttribute("studentList", responses);
//
//        model.addAttribute("studentList", responses);
//
//        return "student/student_list";
//    }



}
