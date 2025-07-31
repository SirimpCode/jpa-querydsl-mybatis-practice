package com.github.jpaquerydslmybatis.web.intercepter;

import com.github.jpaquerydslmybatis.common.myenum.RoleEnum;
import com.github.jpaquerydslmybatis.web.dto.auth.UserInfoResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminCheckInterceptor implements HandlerInterceptor {


    @Override// Object handler 은 요청을 받기를 앞둔 Controller 의 정보가 담겨있다.
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfoResponse loginUser = (UserInfoResponse) request.getSession().getAttribute("loginuser");
        if (loginUser == null) {
            String message = "로그인이 필요한 서비스입니다. 관리자만 접근할 수 있는 서비스입니다.";
            String loc = request.getContextPath() + "/auth/login";
            String msgJsp = request.getContextPath() + "/WEB-INF/views/msg.jsp";
            request.setAttribute("message", message);
            request.setAttribute("loc", loc);
            request.getRequestDispatcher(msgJsp).forward(request, response);
            return false;
        }
        if (loginUser.getRole() != RoleEnum.ROLE_ADMIN ) {
            String message = "권한이 없어요. 관리자만 접근할 수 있는 서비스입니다.";
            String loc = "javascript:history.back()";
            String msgJsp = request.getContextPath() + "/WEB-INF/views/msg.jsp";
            request.setAttribute("message", message);
            request.setAttribute("loc", loc);
            request.getRequestDispatcher(msgJsp).forward(request, response);
            return false; // 요청을 중단
        }


        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
