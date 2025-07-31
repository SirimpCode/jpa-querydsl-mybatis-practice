<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%
  // 들여쓰기용 padding 계산
  int depth = request.getAttribute("depth") == null ? 0 : (Integer)request.getAttribute("depth");
  String padding = (depth * 30) + "px";
%>
<tr>
  <td>${status != null ? status.count : ''}</td>
  <td>${board.boardId}</td>
  <td>
        <span class="subject" style="padding-left: <%=padding%>;" onclick="goView('${board.boardId}')">
            ${board.refinedTitle}
            <c:if test="${board.commentCount > 0}">
              <span style="vertical-align: super;">[<span style="color: red; font-style: italic; font-size: 9pt; font-weight: bold;">${board.commentCount}</span>]</span>
            </c:if>
        </span>
  </td>
  <td>${board.writer}</td>
  <td>${board.createdAt}</td>
  <td>${board.viewCount}</td>
</tr>
<c:if test="${not empty board.childrenBoards}">
  <c:forEach var="child" items="${board.childrenBoards}">
    <jsp:include page="boardRow.jsp">
      <jsp:param name="board" value="${child}" />
      <jsp:param name="depth" value="${depth + 1}" />
    </jsp:include>
  </c:forEach>
</c:if>