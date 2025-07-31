<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
  <title>헤헿</title>
  <style>
    table {
      border-collapse: collapse;
      width: 60%;
      margin: 20px auto;
    }
    th, td {
      border: 1px solid #aaa;
      padding: 8px 12px;
      text-align: center;
    }
    th {
      background: #f0f0f0;
    }
  </style>
</head>
<body>
<h2 style="text-align:center;">학생 목록</h2>
<table>
  <thead>
  <tr>
    <th>학생 ID</th>
    <th>이름</th>
    <th>이메일</th>
  </tr>
  </thead>
  <tbody>
  <c:forEach var="student" items="${studentList}">
    <tr>
      <td>${student.studentId}</td>
      <td>${student.name}</td>
      <td>${student.email}</td>
    </tr>
  </c:forEach>
  </tbody>
</table>
<h2 style="text-align:center;">학생 목록2 arr 버전</h2>
<table>
  <thead>
  <tr>
    <th>학생 ID</th>
    <th>이름</th>
    <th>이메일</th>
  </tr>
  </thead>
  <tbody>
  <c:forEach var="student" items="${studentList2}">
    <tr>
      <td>${student.studentId}</td>
      <td>${student.name}</td>
      <td>${student.email}</td>
    </tr>
  </c:forEach>
  </tbody>
</table>
</body>
</html>