package com.github.jpaquerydslmybatis.web.controller;

import com.github.jpaquerydslmybatis.service.TestService;
import com.github.jpaquerydslmybatis.web.domain.db1.main.MainImageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainViewController {
    private final TestService testService;
    @GetMapping("/main")
    public String mainView(Model model){
        List<MainImageDto> mainImageList = testService.getMainImageList();
        model.addAttribute("mainImageList", mainImageList);
        return "mycontent1/main/index";
    }
}
