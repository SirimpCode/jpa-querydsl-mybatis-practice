<%--
  Created by IntelliJ IDEA.
  User: user
  Date: 25. 7. 17.
  Time: 오전 10:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    String ctxPath = request.getContextPath();
%>
<jsp:include page="../../header/header1.jsp"/>

<script>
    const parentBoardId = '${parentId}'
    const rootBoardId = '${rootId}'
    $(() => {
        <%--스마트 에디터 구현시작--%>
        //전역변수
        var obj = [];

        //스마트에디터 프레임생성
        nhn.husky.EZCreator.createInIFrame({
            oAppRef: obj,
            elPlaceHolder: "content",
            sSkinURI: "<%= ctxPath%>/smarteditor/SmartEditor2Skin.html",
            htParams : {
                // 툴바 사용 여부 (true:사용/ false:사용하지 않음)
                bUseToolbar : true,
                // 입력창 크기 조절바 사용 여부 (true:사용/ false:사용하지 않음)
                bUseVerticalResizer : true,
                // 모드 탭(Editor | HTML | TEXT) 사용 여부 (true:사용/ false:사용하지 않음)
                bUseModeChanger : true,
            }
        });
        <%--스마트 에디터 구현 끄으읏--%>

        // 글쓰기 버튼
        $('button#btnWrite').click(function () {
            <%-- === 스마트 에디터 구현 시작 === --%>
            // id가 content인 textarea에 에디터에서 대입
            obj.getById["content"].exec("UPDATE_CONTENTS_FIELD", []);
            <%-- === 스마트 에디터 구현 끝 === --%>

            const subject = $('input:text[name="subject"]').val().trim();
            if(!subject){
                alert("제목을 입력하세요.");
                $('input:text[name="subject"]').focus();
                return;
            }
            // 글쓰기 폼 유효성 검사 스마트에디터 사용하는경우
            let contentVal = $('textarea[name="content"]').val().trim();

            //p 태그로 감싸져서 나옴 공백만 썼을경우 <p>&nbsp;</p>

            // 정규표현식을 사용해서 유효성검사
            /*
               대상문자열.replace(/찾을 문자열/gi, "변경할 문자열");
            ==> 여기서 꼭 알아야 될 점은 나누기(/)표시안에 넣는 찾을 문자열의 따옴표는 없어야 한다는 점입니다.
                        그리고 뒤의 gi는 다음을 의미합니다.

            g : 전체 모든 문자열을 변경 global
            i : 영문 대소문자를 무시, 모두 일치하는 패턴 검색 ignore
         */
            contentVal = contentVal.replace(/&nbsp;/gi, ""); // 공백문자열을 제거

            // <p>  </p> 로 나온다.


            // const startTemp = contentVal.substring(contentVal.indexOf("<p>") + 3);
            // const lastTemp = startTemp.substring(0, contentVal.indexOf("</p>"));
            // alert("lastTemp: " + lastTemp);//강사님방식임
            contentVal = contentVal.replace(/<p><\/p>/gi, ""); // <p></p> 제거
            if(!contentVal){
                alert("내용을 입력하세요.");
                $('textarea[name="content"]').focus();
                return;
            }

            //글암호 유혀성검사
            const pw = $('input:password[name="pw"]').val();
            if(pw === "") {
                alert("글암호를 입력하세요.");
                $('input:password[name="pw"]').focus();
                return;
            }

            const frm = document.addFrm;


            // 글쓰기 폼 전송
            // axios로 REST API 요청
            const data = {
                title: frm.subject.value,
                content: frm.content.value,
                password: frm.pw.value,
                parentBoardId: parentBoardId,
                rootBoardId: rootBoardId
            };

            axios.post('/api/board', data)
                .then(async response => {
                    console.log(response);
                    const savedId = response.data.success.responseData;


                    //파일 업로드 로직



                    const formInput = frm.file;
                    const file = formInput.files[0];

                    //파일 업로드 함수 정의
                    async function fileUploadFunc() {
                        const formData = new FormData();
                        formData.append('fileList', file); // 파일 첨부
                        formData.append('firstFolder', 'board');
                        formData.append('secondFolder', savedId);
                        formData.append('prefix', '산해바보_');
                        //axios로  파일 업로드 요청
                        try {
                            const fileResponse = await axios.post('/api/storage', formData, {
                                headers: {
                                    'Content-Type': 'multipart/form-data'
                                }
                            });
                            console.log(fileResponse);
                            return fileResponse.data.success.responseData; // 파일 업로드 성공시 반환값
                        } catch (error) {
                            console.error("파일 업로드 실패:", error);
                        }

                    }
                    // 생성된 게시물에 파일정보업데이트시키는 로직
                    async function updateBoardFile(fileName, fileSize, filePath) {
                        const updateData = {
                            primaryKey: savedId,
                            fileName: fileName,
                            fileSize: fileSize,
                            filePath: filePath
                        };
                        try {
                            const updateResponse = await axios.put('/api/board/file', updateData);
                        } catch (error) {
                            console.error("파일 정보 업데이트 실패:", error);
                            throw error; // 에러를 다시 던져서 catch 블록에서 처리
                        }

                    }

                    if (formInput.files.length > 0) {
                        const fileResponse = await fileUploadFunc();
                        const fileName = fileResponse[0].fileName;
                        const fileSize = fileResponse[0].fileSize;
                        const filePath = fileResponse[0].fileUrl;
                        // 게시물에 파일 정보 업데이트
                        await updateBoardFile(fileName, fileSize, filePath);
                    }


                    // 글쓰기 성공 처리
                    alert("글쓰기 성공");
                    location.href = '<%=ctxPath%>/board/view/' + savedId; // 글 목록 페이지로 이동
                })
                .catch(error => {
                    console.log(error);
                    // 글쓰기 실패 처리
                    alert("글쓰기 실패: " + error.response.data.error.customMessage);
                });
        })



    });
</script>

<div style="display: flex;">
    <div style="margin: auto; padding-left:3%;">
        <c:choose>
            <c:when test="${not empty parentId}">
                <h1>답글쓰기</h1>
            </c:when>
            <c:otherwise>
                <h1>글쓰기</h1>
            </c:otherwise>
        </c:choose>
        <form name="addFrm">
            <table style="width: 1024px" class="table table-bordered">
                <tr>
                    <th style="width: 15%; background-color: #DDDDDD;">성명</th>
                    <td>
                        <input type="hidden" name="fk_userid" value="${sessionScope.loginuser.userId}"/>
                        <input type="text" name="name" value="${sessionScope.loginuser.username}" readonly/>
                    </td>
                </tr>
                <tr>
                    <th style="width: 15%; background-color: #DDDDDD;">제목</th>
                    <td>
                        <input type="text" name="subject" size="100" maxlength="200"/>
                    </td>
                </tr>
                <tr>
                    <th style="width: 15%; background-color: #DDDDDD;">내용</th>
                    <td>
                        <textarea style="width: 100%; height: 612px;" name="content" id="content">
                        </textarea>
                    </td>
                </tr>

                <tr>
                    <th style="width: 15%; background-color: #DDDDDD;">파일첨부</th>
                    <td>
                        <input type="file" name="file" />
                    </td>
                </tr>

                <tr>
                    <th style="width: 15%; background-color: #DDDDDD;">글암호</th>
                    <td>
                        <input type="password" name="pw" maxlength="20" />
                    </td>
                </tr>

            </table>
            <div style="margin: 20px;">
                <button type="button" class="btn btn-secondary btn-sm mr-3" id="btnWrite">글쓰기</button>
                <button type="button" class="btn btn-secondary btn-sm" onclick="history.back()">취소</button>
            </div>

        </form>


    </div>


<jsp:include page="../../footer/footer1.jsp"/>