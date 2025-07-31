<%--
  Created by IntelliJ IDEA.
  User: user
  Date: 25. 7. 14.
  Time: 오후 4:26
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<h1>Test Page</h1>
<p>Welcome to the test page!</p>
<c:if test="${not empty testData}">
    <h2>Test Data:</h2>
    <table border="1" cellpadding="5" cellspacing="0">
        <thead>
        <tr>
            <th>Number</th>
            <th>Name</th>
            <th>Write Day</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="data" items="${testData}">
            <tr>
                <td>${data.no}</td>
                <td>${data.name}</td>
                <td>${data.writeday}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</c:if>
</body>
</html>
