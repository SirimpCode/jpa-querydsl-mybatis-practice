package com.github.jpaquerydslmybatis.common.aop;

import com.github.jpaquerydslmybatis.common.myenum.RoleEnum;
import com.github.jpaquerydslmybatis.common.util.MyUtil;
import com.github.jpaquerydslmybatis.service.board.BoardService;
import com.github.jpaquerydslmybatis.service.employees.EmpService;
import com.github.jpaquerydslmybatis.web.dto.auth.UserInfoResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

// 공통관심사 클래스 생성하기
// AOP (Aspect-Oriented Programming) 를 사용하여 공통 관심사를 처리할 수 있습니다.
// 예를 들어, 로깅, 트랜잭션 관리, 보안 등을 처리하는 AOP 클래스를 만들 수 있습니다.
@Aspect
@Component
@RequiredArgsConstructor
// !!! 중요 !!! MyspringApplication 클래스에서 @EnableAspectJAutoProxy 을 기재해야 한다.!!!
public class CommonAop {
    // ===== Before Advice(보조업무) 만들기 ====== //
   /*
       주업무(<예: 글쓰기, 글수정, 댓글쓰기, 직원목록조회 등등>)를 실행하기 앞서서
       이러한 주업무들은 먼저 로그인을 해야만 사용가능한 작업이므로
       주업무에 대한 보조업무<예: 로그인 유무검사> 객체로 로그인 여부를 체크하는
       관심 클래스(Aspect 클래스)를 생성하여 포인트컷(주업무)과 어드바이스(보조업무)를 생성하여
       동작하도록 만들겠다.
   */

    // === Pointcut(주업무)을 설정해야 한다. === //
    //     Pointcut 이란 공통관심사<예: 로그인 유무검사>를 필요로 하는 메소드를 말한다.
    /*com.github.jpaquerydslmybatis.web.controller 패키지와 그 하위 모든 패키지에 있는
Controller로 끝나는 클래스의
*RequiredLogin으로 끝나는 public 메서드를 모두 포함합니다.
접근제어자 public 반환값은 * controller 내의 클래스 도 하위패키지의 모든 클래스 */
    @Pointcut("execution(public * com.github.jpaquerydslmybatis.web.controller..*Controller.*RequiredLogin*(..))")
    public void requiredLogin(){

    }
    @Before("requiredLogin()")
    public void loginCheck(JoinPoint joinPoint) {
        // joinPoint 는 AOP 가 적용된 메소드에 대한 정보를 담고 있습니다.
        // 예를 들어, 메소드 이름, 인자, 클래스 정보 등을 얻을 수 있습니다.
        // 여기서 로그인 유무를 체크하는 로직을 작성합니다.
        System.out.println("로그인 유무를 체크하는 로직이 실행되었습니다.");
        // 로그인 체크를 위해 session 을 얻어 와야 한다.

        HttpServletRequest request = (HttpServletRequest) joinPoint.getArgs()[0];//  첫번째 인자를 가져옵니다.
        HttpServletResponse response = (HttpServletResponse) joinPoint.getArgs()[1];//  두번째 인자를 가져옵니다.

        HttpSession session = request.getSession();
        if(session.getAttribute("loginuser") == null){
            String message = "먼저 로그인 하세요~~ (AOP Before Advice 활용)";
            String loc = request.getContextPath()+"/auth/login";

            request.setAttribute("message", message);
            request.setAttribute("loc", loc);
            //로그인 성공후 로그인 하기전 페이지로 돌아가는 작업 만들기
            String currentURL = MyUtil.getCurrentURL(request);
            session.setAttribute("goBackURL", currentURL); // 현재 URL을 세션에 저장
            System.out.println(currentURL);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/msg.jsp");
            try {
                dispatcher.forward(request, response);
            } catch (ServletException | IOException e) {
                e.printStackTrace();
            }
        }


    }


    // ===== After Advice(보조업무) 만들기 ====== //
   /*
       주업무(<예: 글쓰기, 제품구매 등등>)를 실행한 다음에
       회원의 포인트를 특정점수(예: 100점, 200점, 300점) 증가해 주는 것이 공통의 관심사(보조업무)라고 보자.
       관심 클래스(Aspect 클래스)를 생성하여 포인트컷(주업무)과 어드바이스(보조업무)를 생성하여
       동작하도록 만들겠다.
   */
    // =====  Around Advice(보조업무) 만들기 ====== //

   /*
       Before ----> 보조업무1
              주업무
       After  ----> 보조업무2

            보조업무1 + 보조업무2 을 실행하도록 해주는 것이 Around Advice 이다.
   */
    private final BoardService boardService;
    private final EmpService empService;

    //에프터 리터닝 어드바이스
    @AfterReturning("pointPlus()")
    public void plusProcess(JoinPoint joinPoint) {
        Object[] argsArr = joinPoint.getArgs();
        for (Object args : argsArr) {
            if(args instanceof HttpServletRequest request){
                String userId = (String) request.getAttribute("userId");
                Long plusPoint = (Long) request.getAttribute("plusPoint");
                if(userId == null || plusPoint == null) return;
                boardService.userPointPlus(userId, plusPoint); // 포인트를 100점 증가시킵니다.
                return;
            }
        }
    }
    /*
    * @After  Pointcut 이 적용된 메소드가 실행된 후에 실행됩니다.
    * @AfterReturning  Pointcut 이 적용된 메소드가 정상적으로 실행된 후에 실행됩니다.
    * @AfterThrowing  Pointcut 이 적용된 메소드가 예외를 던진 후에 실행됩니다.
    * */
    @Pointcut("execution(public * com.github.jpaquerydslmybatis.web.controller..*Controller.*PointPlus(..))")
    public void pointPlus(){}

    @Pointcut("execution(public String com.github.jpaquerydslmybatis.web.controller.employees.EmpViewController.*EmpManager(..))")
    public void chartAdvice(){}

    @Around("chartAdvice()")
    public String checkAuthority(ProceedingJoinPoint joinPoint) {
        /* Around Advice 에서는 ProceedingJoinPoint joinPoint 가
                         포인트컷 되어진 주업무의 메소드이다. */
        // ========= 보조업무 1 시작 =========== //
        // - 직원관리와 관련된 주업무를 실행하는 데 있어서 권한이 있는지(로그인 되어진 사용자의 gradelevel 값이 10)를 알아보는것을 보조업무로 보겠다.
        HttpServletRequest request = Arrays.stream(joinPoint.getArgs())
                .filter(arg -> arg instanceof HttpServletRequest)
                .map(arg -> (HttpServletRequest) arg)
                .findFirst().orElse(null);
        if (request == null) return null;
        String currentURL = request.getContextPath()+MyUtil.getCurrentURL(request);

        HttpSession session = request.getSession();
        UserInfoResponse loginUser = (UserInfoResponse) session.getAttribute("loginuser");
        if (loginUser == null || loginUser.getRole() != RoleEnum.ROLE_ADMIN){
            String message = "접근 권한이 없습니다.";
            String loc = loginUser== null ?
                    request.getContextPath()+"/auth/login" :
                    "javascript:history.back()";

            request.setAttribute("message", message);
            request.setAttribute("loc", loc);
            //로그인 성공후 로그인 하기전 페이지로 돌아가는 작업 만들기
            session.setAttribute("goBackURL", currentURL); // 현재 URL을 세션에 저장
            System.out.println(currentURL);

            return "msg";
        }
        // !!!!!! 중요 !!!!!!! 주업무 메소드(포인트컷) 가 실행되는 것이다. !!!!!


        try {
            return (String) joinPoint.proceed(); // 주업무 메소드(포인트컷) 를 실행한다.
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {//catch 에서 throw 를 하더라도 finally 블럭은 실행된다. 그이후에 throw 가 실행된다.
            // ======= 보조업무 2 시작 =========
            // - 인사관리 페이지에 접속한 이후에, 인사관리 페이지에 접속한 페이지URL, 사용자ID, 접속IP주소, 접속시간을 기록으로 DB에 tbl_empManger_accessTime 테이블에 insert 하도록 한다.
            String userIp = request.getRemoteAddr();
            empService.createEmpManagementLog(loginUser.getUserId(), userIp, currentURL);

        }
    }




}
