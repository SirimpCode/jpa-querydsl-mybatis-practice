package com.github.jpaquerydslmybatis.web.controller.auth;

import com.github.jpaquerydslmybatis.service.auth.SignUpAuthService;
import com.github.jpaquerydslmybatis.web.dto.auth.LoginRequest;
import com.github.jpaquerydslmybatis.web.dto.auth.UserInfoResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthViewController {
    private final SignUpAuthService signUpAuthService;
    @GetMapping("/login")
    public String loginPage(){
        return "mycontent1/login/login_form";
    }

    @PostMapping("/login")
    public String loginPage(@ModelAttribute LoginRequest loginRequest, HttpServletRequest request){
        UserInfoResponse userInfoResponse = signUpAuthService.loginLogic(loginRequest, request.getRemoteAddr());
        request.getSession().setAttribute("loginUser", userInfoResponse);
        return "redirect:/main"; // 로그인 성공 후 메인 페이지로 리다이렉트
    }
    @GetMapping("/logout")
    public ModelAndView logoutAfterMainView(HttpServletRequest request, ModelAndView modelAndView){
        request.getSession().invalidate(); // 세션 무효화
        modelAndView.addObject("message", "로그아웃 되었습니다.");
        String loc = request.getContextPath()+"/main"; // 메인 페이지로 리다이렉트
        modelAndView.addObject("loc", loc);

        modelAndView.setViewName("msg");
        return modelAndView; // 로그아웃 후 메인 페이지로 리다이렉트
    }

}
