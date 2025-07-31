package com.github.jpaquerydslmybatis.web.intercepter;

import com.github.jpaquerydslmybatis.web.dto.auth.UserInfoResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/*
   Interceptor 는 Spring 에서 제공해주는 기능이다.
   이 Interceptor 를 통해서 특정 URL 요청이 Controller 에서 실행하기전에 먼저 가로채서 다른 특정 작업을 진행할 수 있도록 해준다.
   이를 통해서 특정 URL 요청의 전,후 처리가 가능해진다.
*/
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override// Object handler 은 요청을 받기를 앞둔 Controller 의 정보가 담겨있다.
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfoResponse loginUser = (UserInfoResponse) request.getSession().getAttribute("loginuser");
        if (loginUser == null) {
            String message = "로그인이 필요한 서비스입니다. 인터셉터에서 잡힘";
            String msgJsp = request.getContextPath() + "/WEB-INF/views/msg.jsp";
            String loc = request.getContextPath() + "/auth/login";
            request.setAttribute("message", message);
            request.setAttribute("loc", loc);
            request.getRequestDispatcher(msgJsp).forward(request, response);
            return false; // 요청을 중단
        }


        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
/*
         [참고]
         postHandle() 메소드 : 컨트롤러가 실행된 후에 호출된다.
                             그러므로 컨트롤러에서 예외가 발생한다면 실행되지 않는다.
         @Override
         public void postHandle(
               HttpServletRequest request, HttpServletResponse response,
               Object obj, ModelAndView mav)
               throws Exception {

         }


          afterCompletion() 메소드 : 뷰에서 최종 결과가 생성하는 일을 포함한 모든 일이 완료 되었을 때 실행된다.
          @Override
          public void afterCompletion(
               HttpServletRequest request, HttpServletResponse response,
               Object obj, Exception e)
               throws Exception {

         }
      */