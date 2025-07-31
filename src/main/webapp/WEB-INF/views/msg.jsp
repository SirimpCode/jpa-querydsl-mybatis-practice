<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<script type="text/javascript">

    alert("${requestScope.message}");      // 메시지 출력해주기
    const loc = !"${requestScope.loc}" || "${requestScope.loc}" === "null" ?
        "${pageContext.request.contextPath}/main" : "${requestScope.loc}"; // loc가 없거나 null 이면 /main 으로 설정
    location.href = loc;

    if (${not empty requestScope.popup_close && requestScope.popup_close == true}) {

        //   opener.location.reload(true); // 부모창 새로고침
        opener.history.go(0);         // 부모창 새로고침
        self.close(); // 팝업창 닫기
    }

</script>