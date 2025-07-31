<%@ page import="com.github.jpaquerydslmybatis.web.dto.auth.UserInfoResponse" %><%--
  Created by IntelliJ IDEA.
  User: user
  Date: 25. 7. 16.
  Time: 오전 10:19
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="../../header/header1.jsp"/>
<%--캐러ㅈ셀 보여주기디이--%>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-10 offset-md-1">
            <div id="carouselExampleIndicators" class="carousel slide"
                 data-ride="carousel">
                <ol class="carousel-indicators">
                    <%--
                                        <li data-target="#carouselExampleIndicators" data-slide-to="0"
                                            class="active"></li>
                                        <li data-target="#carouselExampleIndicators" data-slide-to="1"></li>
                                        <li data-target="#carouselExampleIndicators" data-slide-to="2"></li>
                    --%>
                    <c:if test="${mainImageList != null}">
                        <c:forEach var="images" items="${mainImageList}" varStatus="status">
                            <c:if test="${status.first}">

                            </c:if>
                            <c:if test="${not status.first}">

                            </c:if>
                        </c:forEach>
                    </c:if>
                </ol>
                <div class="carousel-inner">
                    <%--                    <div class="carousel-item active">--%>
                    <%--                        <img src="images/Koala.jpg" class="d-block w-100" alt="...">--%>
                    <%--                        <!-- d-block 은 display: block; 이고  w-100 은 width 의 크기는 <div class="carousel-item active">의 width 100% 로 잡으라는 것이다. -->--%>
                    <%--                        <div class="carousel-caption d-none d-md-block">--%>
                    <%--                            <!-- d-none 은 display : none; 이므로 화면에 보이지 않다가, d-md-block 이므로 d-md-block 은 width 가 768px이상인 것에서만 display: block; 으로 보여라는 말이다.  -->--%>
                    <%--                            <h5>Koala</h5>--%>
                    <%--                            <p>Koala Content</p>--%>
                    <%--                        </div>--%>
                    <%--                    </div>--%>
                    <%--                    <div class="carousel-item">--%>
                    <%--                        <img src="../images/Lighthouse.jpg" class="d-block w-100" alt="...">--%>
                    <%--                        <div class="carousel-caption d-none d-md-block">--%>
                    <%--                            <h5>Lighthouse</h5>--%>
                    <%--                            <p>Lighthouse Content</p>--%>
                    <%--                        </div>--%>
                    <%--                    </div>--%>
                    <%--                    <div class="carousel-item">--%>
                    <%--                        <img src="../images/Penguins.jpg" class="d-block w-100" alt="...">--%>
                    <%--                        <div class="carousel-caption d-none d-md-block">--%>
                    <%--                            <h5>Penguins</h5>--%>
                    <%--                            <p>Penguins Content</p>--%>
                    <%--                        </div>--%>
                    <%--                    </div>--%>
                    <c:if test="${mainImageList != null}">
                        <c:forEach var="images" items="${mainImageList}" varStatus="status">
                            <c:if test="${status.first}">
                                <div class="carousel-item active">
                                    <img src="images/${images.imageFileName}" class="d-block w-100" alt="...">
                                    <!-- d-block 은 display: block; 이고  w-100 은 width 의 크기는 <div class="carousel-item active">의 width 100% 로 잡으라는 것이다. -->
                                    <div class="carousel-caption d-none d-md-block">
                                        <!-- d-none 은 display : none; 이므로 화면에 보이지 않다가, d-md-block 이므로 d-md-block 은 width 가 768px이상인 것에서만 display: block; 으로 보여라는 말이다.  -->
                                        <h5>${images.imageName}</h5>
                                        <p>${images.imageName}</p>
                                    </div>
                                </div>

                            </c:if>
                            <c:if test="${not status.first}">
                                <div class="carousel-item">
                                    <img src="images/${images.imageFileName}" class="d-block w-100" alt="...">
                                    <!-- d-block 은 display: block; 이고  w-100 은 width 의 크기는 <div class="carousel-item active">의 width 100% 로 잡으라는 것이다. -->
                                    <div class="carousel-caption d-none d-md-block">
                                        <!-- d-none 은 display : none; 이므로 화면에 보이지 않다가, d-md-block 이므로 d-md-block 은 width 가 768px이상인 것에서만 display: block; 으로 보여라는 말이다.  -->
                                        <h5>${images.imageName}</h5>
                                        <p>${images.imageName}</p>
                                    </div>
                                </div>
                            </c:if>
                        </c:forEach>
                    </c:if>
                </div>
                <a class="carousel-control-prev" href="#carouselExampleIndicators"
                   role="button" data-slide="prev"> <span
                        class="carousel-control-prev-icon" aria-hidden="true"></span> <span
                        class="sr-only">Previous</span>
                </a> <a class="carousel-control-next" href="#carouselExampleIndicators"
                        role="button" data-slide="next"> <span
                    class="carousel-control-next-icon" aria-hidden="true"></span> <span
                    class="sr-only">Next</span>
            </a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../../footer/footer1.jsp"/>
