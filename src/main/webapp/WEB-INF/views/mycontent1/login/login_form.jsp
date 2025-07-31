<%--
  Created by IntelliJ IDEA.
  User: user
  Date: 25. 7. 16.
  Time: 오후 12:23
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String ctxPath = request.getContextPath();
%>
<jsp:include page="../../header/header1.jsp"/>
<script>
    $(() => {
        const funcLogin = function () {
            const userid = $("input#userid").val();
            const pwd = $("input#pwd").val();

            if (userid.trim() == "") {
                alert("아이디를 입력하세요!!");
                $("input#userid").val("");
                $("input#userid").focus();
                return; // 종료
            }

            if (pwd.trim() == "") {
                alert("비밀번호를 입력하세요!!");
                $("input#pwd").val("");
                $("input#pwd").focus();
                return; // 종료
            }


// axios로 REST API 요청
            axios.post('<%=ctxPath%>/api/auth/login', {
                userid: userid,
                pwd: pwd
            })
                .then(response => {
                    console.log(response);
                    // 로그인 성공 처리
                    const responseData = response.data.success.responseData;
                    if (responseData.passwordChange)
                        alert("비밀번호 변경한지 3개월 지났음 변경해")
                    else
                        alert("로그인 성공");
                    if(responseData.goBackURL)
                        location.href = responseData.goBackURL;
                    else
                        location.href = "/main";
                })
                .catch(error => {
                    console.log(error);
                    if (error.status === 404){
                        alert(`입력하신 \${error.response.data.error.request} 로 가입된 계정이 존재 하지 않습니다.`);
                        return;
                    }
                    // 로그인 실패 처리
                    alert("로그인 실패: " + error.response.data.error.customMessage);

                });
        }


        $("button#btnLOGIN").click(function () {
            funcLogin();
        })
        $("input:password[id='pwd']").keyup(function (e) {
            if (e.keyCode === 13) { // Enter key
                $("button#btnLOGIN").trigger("click");
            }
        })

    })
</script>
<div class="container">
    <div class="row" style="display: flex; border: solid 0px red;">
        <div class="col-md-8 col-md-offset-2" style="margin: auto; border: solid 0px blue;">
            <h2 class="text-muted">로그인</h2>
            <hr style="border: solid 1px orange">

            <form name="loginFrm" class="mt-5">
                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label class="text-muted font-weight-bold" for="userid">아이디</label>
                        <input type="text" class="form-control" name="userid" id="userid"
                               value=""/> <%-- 부트스트랩에서 input 태그에는 항상 class form-control 이 사용되어져야 한다. --%>
                    </div>

                    <div class="form-group col-md-6">
                        <label class="text-muted font-weight-bold text-muted" for="pwd">비밀번호</label>
                        <input type="password" class="form-control" name="pwd" id="pwd" value=""/>
                    </div>
                </div>
            </form>
        </div>
        <div class="col-md-8 col-md-offset-2" style="margin: auto; display: flex; border: solid 0px blue;">
            <div style="margin: auto; border: solid 0px blue;">
                <button style="width: 150px; height: 40px;" class="btn btn-primary" type="button" id="btnLOGIN">확인
                </button>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../../footer/footer1.jsp"/>