<%--
  Created by IntelliJ IDEA.
  User: user
  Date: 25. 7. 28.
  Time: 오후 12:28
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- ======= sideinfo 페이지 만들기  ======= --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
  String ctxPath = request.getContextPath();
  //     /myspring
%>
<style>

</style>
<script>
  $(()=>{
    // 현재시간을 보여주는 함수 호출
    loopshowNowTime();
  })//돔로드후 실행함수 끗

  function loopshowNowTime() {
    showNowTime();

    const timejugi = 1000;  // 시간을 1초 마다 자동 갱신하려고.

    setTimeout(function() {
      loopshowNowTime();
    }, timejugi);

  } // end of loopshowNowTime() --------------------------
  // Function Declaration

  function showNowTime() {

    const now = new Date();

    let month = now.getMonth() + 1;
    if(month < 10) {
      month = "0"+month;
    }

    let date = now.getDate();
    if(date < 10) {
      date = "0"+date;
    }

    let strNow = now.getFullYear() + "-" + month + "-" + date;

    let hour = "";
    if(now.getHours() < 10) {
      hour = "0"+now.getHours();
    }
    else {
      hour = now.getHours();
    }


    let minute = "";
    if(now.getMinutes() < 10) {
      minute = "0"+now.getMinutes();
    } else {
      minute = now.getMinutes();
    }

    let second = "";
    if(now.getSeconds() < 10) {
      second = "0"+now.getSeconds();
    } else {
      second = now.getSeconds();
    }

    strNow += " "+hour + ":" + minute + ":" + second;

    $("span#clock").html(strNow);

  }// end of function showNowTime() -----------------------------
</script>


<div style="min-height: 1100px; margin-bottom: 50px;">
  <div style="text-align: center; font-size: 16pt;">
    현재시각 :&nbsp; <span id="clock" style="color:green; font-weight:bold;"></span>
  </div>

  <div id="displayWeather" style="min-width: 90%; max-height: 500px; overflow-y: scroll; margin-top: 40px; margin-bottom: 70px; padding-left: 10px; padding-right: 10px;"></div>

  <div style="margin: 20px;">
    <%-- 차트그리기 --%>
    <figure class="highcharts-figure">
      <div id="weather_chart_container"></div>
    </figure>
  </div>
</div>