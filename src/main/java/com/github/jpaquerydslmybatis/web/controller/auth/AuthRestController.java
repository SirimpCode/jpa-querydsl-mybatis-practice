package com.github.jpaquerydslmybatis.web.controller.auth;

import com.github.jpaquerydslmybatis.common.util.MyUtil;
import com.github.jpaquerydslmybatis.service.auth.SignUpAuthService;
import com.github.jpaquerydslmybatis.web.dto.auth.LoginResponse;
import com.github.jpaquerydslmybatis.web.dto.response.CustomResponse;
import com.github.jpaquerydslmybatis.web.dto.auth.LoginRequest;
import com.github.jpaquerydslmybatis.web.dto.auth.UserInfoResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {
    private final SignUpAuthService signUpAuthService;
    @GetMapping("/test")
    public List<UserInfoResponse> test() {
        return signUpAuthService.getAllUserInfo();
    }

    @PostMapping("/login")
    public CustomResponse<LoginResponse> login(@RequestBody LoginRequest loginRequest,
                                             HttpServletRequest request) {
        UserInfoResponse userInfo = signUpAuthService.loginLogic(loginRequest, request.getRemoteAddr());
        HttpSession httpSession = request.getSession();
        httpSession.setAttribute("loginuser", userInfo);
        // 로그인을 해야만 접근할 수 있는 페이지에 로그인을 하지 않은 상태에서 접근을 시도한 경우
        // "먼저 로그인을 하세요!!" 라는 메시지를 받고서 사용자가 로그인을 성공했다라면
        // 화면에 보여주는 페이지는 시작페이지로 가는 것이 아니라
        // 조금전 사용자가 시도하였던 로그인을 해야만 접근할 수 있는 페이지로 가기 위한 것이다.
        String goBackURL = httpSession.getAttribute("goBackURL") != null ?
                (String) httpSession.getAttribute("goBackURL") : null;

        return CustomResponse.ofOk("로그인 성공", LoginResponse.of(userInfo.isPasswordChange(), goBackURL));

    }


}
