<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<%-- ===== header 페이지 만들기 ===== --%>
<%
    String ctxPath = request.getContextPath();
    //     /myspring
%>

<%-- 상단 네비게이션 시작 --%>
<nav class="navbar navbar-expand-lg navbar-light bg-light fixed-top mx-4 py-3">
    <!-- Brand/logo -->
    <a class="navbar-brand" href="<%=ctxPath%>/main" style="margin-right: 5%;"><img
            src="<%=ctxPath%>/images/sist_logo.png"/></a>

    <!-- 아코디언 같은 Navigation Bar 만들기 -->
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#collapsibleNavbar">
        <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="collapsibleNavbar">
        <ul class="navbar-nav h6"> <%-- .h6 는 글자크기임 --%>
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle text-info" href="#" id="navbarDropdown"
                   data-toggle="dropdown">Home</a>
                <%-- .text-info 는 글자색으로 청록색임 --%>
                <div class="dropdown-menu" aria-labelledby="navbarDropdown">
                    <a class="dropdown-item" href="<%=ctxPath%>/index">Home</a>
                </div>
            </li>

            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle text-info" href="#" id="navbarDropdown"
                   data-toggle="dropdown">게시판</a>
                <div class="dropdown-menu" aria-labelledby="navbarDropdown">
                    <a class="dropdown-item" href="<%=ctxPath%>/board/list/season2">목록보기</a>
                    <%-- <c:if test="${not empty sessionScope.loginuser}"> --%>
                    <a class="dropdown-item" href="<%=ctxPath%>/board/write">글쓰기</a>
                    <a class="dropdown-item" href="<%=ctxPath%>/board/myComment">댓글쓰기</a>
                    <%-- </c:if>  --%>
                </div>
            </li>

            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle text-info" href="#" id="navbarDropdown"
                   data-toggle="dropdown">로그인</a>
                <div class="dropdown-menu" aria-labelledby="navbarDropdown">
                    <c:if test="${empty sessionScope.loginuser}">
                        <a class="dropdown-item" href="#">회원가입</a>
                        <a class="dropdown-item" href="<%=ctxPath%>/auth/login">로그인</a>
                    </c:if>

                    <c:if test="${not empty sessionScope.loginuser}">
                        <a class="dropdown-item" href="#">나의정보</a>
                        <a class="dropdown-item" href="<%=ctxPath%>/auth/logout">로그아웃</a>
                    </c:if>
                </div>
            </li>

            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle text-info" href="#" id="navbarDropdown"
                   data-toggle="dropdown">인사관리</a>
                <div class="dropdown-menu" aria-labelledby="navbarDropdown">
                    <a class="dropdown-item" href="<%=ctxPath%>/emp/list">직원목록</a>
                    <a class="dropdown-item" href="<%=ctxPath%>/emp/chart">통계차트</a>
                </div>
            </li>

            <%-- <c:if test="${sessionScope.loginuser.gradelevel == 10 }">  --%>
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle text-info" href="#" id="navbarDropdown" data-toggle="dropdown">메일보내기(다중파일첨부)</a>
                <div class="dropdown-menu" aria-labelledby="navbarDropdown">
                    <a class="dropdown-item" href="<%=ctxPath%>/email/write">email작성</a>
                </div>
            </li>
            <%-- </c:if>  --%>

            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle text-info" href="#" id="navbarDropdown"
                   data-toggle="dropdown">일정관리</a>
                <div class="dropdown-menu" aria-labelledby="navbarDropdown">
                    <a class="dropdown-item" href="<%=ctxPath%>/schedule/scheduleManagement">일정관리</a>
                </div>
            </li>

            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle text-info" href="#" id="navbarDropdown"
                   data-toggle="dropdown">공공데이터</a>
                <div class="dropdown-menu" aria-labelledby="navbarDropdown">
                    <a class="dropdown-item" href="<%=ctxPath%>/opendata/korea_tour_api">한국관광공사사진</a>
                    <a class="dropdown-item" href="<%=ctxPath%>/opendata/seoul_bicycle_rental">서울따릉이지도</a>
                    <a class="dropdown-item" href="<%=ctxPath%>/opendata/seoul_bicycle_rental_insert">오라클입력및조회</a>
                </div>
            </li>

            <!-- ==== 인터셉터 알아보기 ====  -->
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle text-info" href="#" id="navbarDropdown" data-toggle="dropdown">인터셉터알아보기</a>
                <div class="dropdown-menu" aria-labelledby="navbarDropdown">
                    <a class="dropdown-item" href="<%=ctxPath%>/interceptor/anyone/anyone_a">누구나접근_A</a>
                    <a class="dropdown-item" href="<%=ctxPath%>/interceptor/anyone/anyone_b">누구나접근_B</a>
                    <a class="dropdown-item" href="<%=ctxPath%>/interceptor/member_only/member_a">회원누구나접근_A</a>
                    <a class="dropdown-item" href="<%=ctxPath%>/interceptor/member_only/member_b">회원누구나접근_B</a>
                    <a class="dropdown-item" href="<%=ctxPath%>/interceptor/special_member/special_member_a">특정회원만접근_A</a>
                    <a class="dropdown-item" href="<%=ctxPath%>/interceptor/special_member/special_member_b">특정회원만접근_B</a>
                </div>
            </li>

        </ul>
    </div>

    <%-- === #19. 로그인이 성공되어지면 로그인되어진 사용자의 이메일 주소를 출력하기 === --%>
    <c:if test="${not empty sessionScope.loginuser}">
        <div style="float: right; font-size: 9pt;">
            <span style="color: navy; font-weight: bold;">${sessionScope.loginuser.email}</span> 님<br>로그인중..
        </div>
    </c:if>

</nav>
<%-- 상단 네비게이션 끝 --%>


<p class="h5" style="margin: auto">
    <marquee> [공지사항] 쌍용교육센터 7강의실 수강생 여러분들의 수료후 프로그래머 입사를 진심으로 기원합니다.</marquee>
</p>
    