<%@ page import="com.github.jpaquerydslmybatis.web.dto.auth.UserInfoResponse" %><%--
  Created by IntelliJ IDEA.
  User: user
  Date: 25. 7. 16.
  Time: 오전 10:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String contextPath = request.getContextPath();
    Object loginUserObj = session.getAttribute("loginuser");
    UserInfoResponse loginUser = loginUserObj != null ? (UserInfoResponse) loginUserObj : null;
%>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <%--     Bootstrap CSS--%>
    <link rel="stylesheet" type="text/css" href="<%= contextPath %>/bootstrap/css/bootstrap.min.css">

    <%-- Font Awesome 6 Icons --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">

    <%-- 직접 만든 CSS 2 --%>
    <link rel="stylesheet" type="text/css" href="<%=contextPath%>/css/style2.css" />
    <%-- Optional JavaScript --%>
    <script type="text/javascript" src="<%=contextPath%>/js/jquery-3.7.1.min.js"></script>
    <script type="text/javascript" src="<%=contextPath%>/bootstrap/js/bootstrap.bundle.min.js" ></script>
    <script type="text/javascript" src="<%=contextPath%>/smarteditor/js/HuskyEZCreator.js" charset="utf-8"></script>

    <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
    <%--    sweetalert--%>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

    <title>SpringBoot2</title>
</head>
<body>
<div id="mycontainer">
    <div id="myheader">
        <jsp:include page="./menu/menu2.jsp" />
    </div>

    <div id="mycontent">
