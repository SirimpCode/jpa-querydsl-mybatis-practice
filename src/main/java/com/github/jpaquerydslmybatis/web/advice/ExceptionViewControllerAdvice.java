package com.github.jpaquerydslmybatis.web.advice;

import com.github.jpaquerydslmybatis.common.exception.CustomViewException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionViewControllerAdvice {
    @ExceptionHandler(CustomViewException.class)
    public String handleCustomViewException(CustomViewException e, Model model) {
        model.addAttribute("message", e.getMessage());
        model.addAttribute("loc", e.getLoc());
        return "msg"; // 에러 페이지의 뷰 이름을 반환합니다.
    }
}
