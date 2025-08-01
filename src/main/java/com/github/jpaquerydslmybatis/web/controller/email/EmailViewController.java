package com.github.jpaquerydslmybatis.web.controller.email;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// 다중 파일 첨부가 있는 복수 사용자에게 이메일 보내기
@Controller
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailViewController {

    @GetMapping("/write")
    public String emailWrite(){
        return "mycontent1/email/emailWrite";
    }
    @GetMapping("/done")
    public String emailDone(){
        return "mycontent1/email/emailDone";
    }
}
