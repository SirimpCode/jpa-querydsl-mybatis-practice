<%--
  Created by IntelliJ IDEA.
  User: user
  Date: 25. 7. 17.
  Time: 오후 5:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
    String ctxPath = request.getContextPath();
    //     /myspring
%>

<jsp:include page="../../header/header1.jsp"/>

<style type="text/css">

    span.move {
        cursor: pointer;
        color: navy;
    }

    .moveColor {
        color: #660029;
        font-weight: bold;
        background-color: #ffffe6;
    }

    td.myComment {
        text-align: center;
    }

    a {
        text-decoration: none !important;
    }
</style>

<script type="text/javascript">
    const loginUserId = "${sessionScope.loginuser != null ? sessionScope.loginuser.userId : ''}";
    const boardId = ${board.boardId};
    const parentBoardId = ${board.parentBoardId != null ? board.parentBoardId : 0};
    const rootBoardId = ${board.rootBoardId != null ? board.rootBoardId : 0};
    $(() => {
        goReadCommentList(boardId, 1); // 페이지 1로 초기화
        $('span.move').hover(function (e) {
            $(e.target).addClass('moveColor');
        }, function (e) {
            $(e.target).removeClass('moveColor');
        })
        $(document).on('keyup', 'form#addWriteFrm input[name="content"]', function (e) {
            if (e.keyCode === 13) {
                goAddWrite();
                $(this).val(''); // 입력 후 내용 비우기
            }
        });

    })//시작후 펑션끗

    //function declation
    function goView(boardId) {
        const form = document.goViewForm;
        form.action = '<%=ctxPath%>/board/view/' + boardId;
        form.method = 'post';
        form.boardId.value = boardId;
        // 글번호로 글보기 페이지로 이동
        form.submit();
    }

    //댓글쓰기
    function goAddWrite() {
        const commentValue = $('input:text[name="content"]').val().trim();
        if (commentValue === "") {
            alert("댓글내용을 입력하세요");
            $('input:text[name="content"]').focus();
            return;
        }

        goAddWriteWithFile();


    }//댓글쓰기끝

    //첨부파일 없는 함수
    function goAddWriteWithFile() {

        const form = document.addWriteFrm;
        // const data = new FormData(form);
        const data = {
            userId: form.fk_userid.value,
            content: form.content.value,
            boardId: form.boardId.value
            // 파일 첨부가 필요하다면 별도 처리 필요
        };

        // axios로 REST API 요청
        axios.post('<%=ctxPath%>/api/comment',  JSON.stringify(data), {
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(async response => {
                console.log(response);
                const savedComment = response.data.success.responseData;
                const savedCommentId = savedComment.commentId;

                const formInput = form.file;
                const file = formInput.files[0];


                //파일 업로드 함수
                async function fileUploadFunc() {
                    const fileData = new FormData();
                    fileData.append('fileList', file); // 파일 첨부
                    fileData.append('firstFolder', 'comment');
                    fileData.append('secondFolder', savedCommentId); // 댓글 ID를 폴더명으로 사용
                    fileData.append('prefix', '산해바보_'); // 파일명 접두사
                    // axios로 파일 업로드 요청
                    try {
                        const fileResponse = await axios.post('<%=ctxPath%>/api/storage', fileData, {
                            headers: {
                                'Content-Type': 'multipart/form-data'
                            }
                        });
                        return fileResponse.data.success.responseData; // 파일 업로드 성공시 반환값
                    } catch (error) {
                        console.error("파일 업로드 실패:", error);
                        alert("파일 업로드 실패: " + error.response.data.error.customMessage);
                        throw error; // 에러를 다시 던져서 catch 블록에서 처리
                    }
                }
                // 생성된 게시물에 파일정보업데이트시키는 로직
                async function updateBoardFile(fileName, fileSize, filePath) {
                    const updateData = {
                        primaryKey: savedCommentId,
                        fileName: fileName,
                        fileSize: fileSize,
                        filePath: filePath
                    };
                    try {
                        const updateResponse = await axios.put('/api/comment/file', updateData);
                    } catch (error) {
                        console.error("파일 정보 업데이트 실패:", error);
                        throw error; // 에러를 다시 던져서 catch 블록에서 처리
                    }
                }
                //파일이있을경우에만 위의 함수호출
                if (formInput.files.length > 0) {
                    const fileResponse = await fileUploadFunc();
                    const fileName = fileResponse[0].fileName;
                    const fileSize = fileResponse[0].fileSize;
                    const filePath = fileResponse[0].fileUrl;
                    // 게시물에 파일 정보 업데이트
                    await updateBoardFile(fileName, fileSize, filePath);

                    alert("댓글쓰기 성공");
                    // 댓글 목록을 다시 불러오기
                    goReadCommentList(boardId, 1);
                    // 폼 초기화
                    form.content.value = '';
                    form.file.value = ''; // 파일 입력 초기화
                    return;
                }
                alert("댓글쓰기 성공");
                // 댓글 목록을 다시 불러오기
                goReadCommentList(boardId, 1);
                // 폼 초기화
                form.content.value = '';

            })
            .catch(error => {
                console.log(error);
                // 댓글쓰기 실패 처리
                alert("댓글쓰기 실패: " + error.response.data.error.customMessage);
            });
    }



    function goReadCommentList(boardId, page) {

        axios.get('<%=ctxPath%>/api/comment', {
            params: {
                boardId: boardId,
                page: page
            }
        }).then((response) => {
            console.log(response);
            const commentList = response.data.success.responseData.elements;
            const totalPage = response.data.success.responseData.totalPages;
            const hasNext = response.data.success.responseData.hasNext;
            const totalElements = response.data.success.responseData.totalElements;
            displayCommentList(commentList, page, 10);
            // 페이지바 표시
            displayPageBar(totalPage, hasNext, totalElements, page);

        }).catch(error => {
            console.log(error);
            alert("댓글 목록 조회 실패: " + error.response.data.error.customMessage);
        });
    }

    function displayPageBar(totalPage, hasNext, totalElements, currentPage) {
        let pageBar = '';
        const pageBarSize = 5;

        const totalPages = parseInt(totalPage);
        const current = parseInt(currentPage);
        const startPage = Math.floor((current - 1) / pageBarSize) * pageBarSize + 1;
        const endPage = Math.min(startPage + pageBarSize - 1, totalPages);

        pageBar += '<nav aria-label="Page navigation">';
        pageBar += '<ul class="pagination justify-content-center">';

        // [≪] 맨앞으로
        if (current > 1) {
            pageBar += `
            <li class="page-item">
                <a class="page-link" href="javascript:goToPage(1)" aria-label="First">
                    <span aria-hidden="true">&laquo;&laquo;</span>
                </a>
            </li>`;
        } else {
            pageBar += `
            <li class="page-item disabled">
                <a class="page-link" href="#" tabindex="-1" aria-disabled="true">
                    <span aria-hidden="true">&laquo;&laquo;</span>
                </a>
            </li>`;
        }

        // [<] 이전 페이지 그룹
        if (startPage > 1) {
            pageBar += `
            <li class="page-item">
                <a class="page-link" href="javascript:goToPage(\${startPage - 1})" aria-label="Previous">
                    <span aria-hidden="true">&laquo;</span>
                </a>
            </li>`;
        } else {
            pageBar += `
            <li class="page-item disabled">
                <a class="page-link" href="#" tabindex="-1" aria-disabled="true">
                    <span aria-hidden="true">&laquo;</span>
                </a>
            </li>`;
        }

        // 페이지 번호들
        for (let i = startPage; i <= endPage; i++) {
            if (i === current) {
                pageBar += `<li class="page-item active"><span class="page-link">\${i}</span></li>`;
            } else {
                pageBar += `<li class="page-item"><a class="page-link" href="javascript:goToPage(\${i})">\${i}</a></li>`;
            }
        }

        // [>] 다음 페이지 그룹
        if (endPage < totalPages) {
            pageBar += `
            <li class="page-item">
                <a class="page-link" href="javascript:goToPage(\${endPage + 1})" aria-label="Next">
                    <span aria-hidden="true">&raquo;</span>
                </a>
            </li>`;
        } else {
            pageBar += `
            <li class="page-item disabled">
                <a class="page-link" href="#" tabindex="-1" aria-disabled="true">
                    <span aria-hidden="true">&raquo;</span>
                </a>
            </li>`;
        }

        // [≫] 맨뒤로
        if (current < totalPages) {
            pageBar += `
            <li class="page-item">
                <a class="page-link" href="javascript:goToPage(\${totalPages})" aria-label="Last">
                    <span aria-hidden="true">&raquo;&raquo;</span>
                </a>
            </li>`;
        } else {
            pageBar += `
            <li class="page-item disabled">
                <a class="page-link" href="#" tabindex="-1" aria-disabled="true">
                    <span aria-hidden="true">&raquo;&raquo;</span>
                </a>
            </li>`;
        }

        pageBar += '</ul></nav>';

        document.getElementById("pageBar").innerHTML = pageBar;
    }


    function goToPage(pageNumber) {
        // 여기에 실제 페이지를 바꾸는 로직을 작성 (예: AJAX 재요청)
        goReadCommentList(boardId, pageNumber);
    }


    function showFormChildrenComment(childrenCommentBtn) {
        const 승호바보 = true;
        //alert("승호바보"+승호바보);
        const index = $('button.btn-children-comment').index(childrenCommentBtn);
        //alert(index);
        const $forms = $('form[name="formChildrenComment"]');
        $forms.hide().eq(index).show();

    }

    function showChildren(childDivId) {
        const $childDiv = $('#' + childDivId);
        if ($childDiv.is(':visible')) {
            $childDiv.slideUp(300); // 0.3초 자연스럽게 닫힘
        } else {
            $childDiv.slideDown(300); // 0.3초 자연스럽게 열림
        }
    }

    function displayCommentList(commentList, page, size) {
        console.log("댓글 목록: ");
        console.log(commentList);
        let commentHtml = '';
        if (commentList.length > 0) {
            commentList.forEach((element, index) => {

                const rowNumber = index + 1 + (page - 1) * size;

                //console.log("대댓글수"+ element.childrenComments.length);
                const childCount = element.childrenComments.length;
                commentHtml += `<tr>
					                 <td>\${rowNumber}</td>
					                 <td>\${element.content}</td>
					                 <td>`;

                if (childCount > 0) {
                    commentHtml += `<span style="cursor: pointer" class="badge badge-danger" onclick="showChildren('childShowDiv\${element.commentId}')">\${childCount}</span>&nbsp`;
                }
                if (loginUserId) {
                    commentHtml += `<button type="button" class="btn btn-secondary btn-sm btn-children-comment" onclick="showFormChildrenComment(this)">쓰기</button>`;
                }
                const fileTag = element.fileName ?
                    ('${sessionScope.loginuser}' ? `<a href="<%=ctxPath%>/api/storage/download?filePath=\${element.filePath}&fileName=\${element.fileName}">\${element.fileName}</a>` :
                    `<a style="cursor: pointer; color: #1e74ef" onclick="alert('로그인 후 다운로드 가능합니다.'); return false;">\${element.fileName}</a>`) :
                    '첨부파일 없음';
                //3자리마다 콤마찍기
                const fileSize = element.fileSize ? `\${element.fileSize.toLocaleString()} bytes` : '';


                commentHtml += `</td>
					                 <td>\${fileTag}</td>
					                 <td>\${fileSize}</td>
					                 <td class='comment'>\${element.username}</td>
					                 <td class='comment'>\${element.createdAt}</td>`;


                if (loginUserId && loginUserId === element.userId) {

                    commentHtml += `<td class='comment'>
        <button type='button' class='btn btn-secondary btn-sm btnModifyComment' data-comment-id='\${element.commentId}'>수정</button>
        <button type='button' class='btn btn-secondary btn-sm btnDeleteComment' data-comment-id='\${element.commentId}'>삭제</button>
        </td>`;
                } else {
                    commentHtml += `<td>&nbsp;</td>`;
                }

                commentHtml += `</tr>

        <tr class="dd-show-reply">`

                let childrenTag = '';
                $.each(element.childrenComments, function (i, childComment) {
                    childrenTag += `<tr><td>↪️&nbsp;\${childComment.content}</td><td>\${childComment.createdAt}</td></tr>`;
                })
                commentHtml += `<td colspan="8">
<div id="childShowDiv\${element.commentId}">
<table width="100%" id='childDiv\${element.commentId}'>
\${childrenTag}
</table></div></td>`


                commentHtml += `</tr>
        <tr>
        <td colspan="8">
        <!-- 대댓글쓰기폼-->
        <form name='formChildrenComment' style="display: none; clear: both;">
        <input type="text" name='content' maxlength="1000" size="120" style="height:50px;"/>
        <input type="text" style='display: none'/>
        <input type="hidden" name='parentId' value="\${element.commentId}"/>
        <button type="button" class="btn btn-primary btn-sm ml-4 mr-2 btn-daedaek-ok" onclick="childrenCommentWriteOk(this)">완료</button>
<button type="button" class="btn btn-danger btn-sm btn-daedaek-cancel" onclick="childrenCommentWriteCancel(this)">취소</button>
        </form>
        </td>
        </tr>


        `;
            })//포이치 끝
        } else {
            commentHtml = `<tr>
				                <td colspan='8'>댓글이 없습니다</td>
				             </tr>`;
        }
        $('tbody#commentDisplay').html(commentHtml);
        $(document).on('keyup', 'form[name="formChildrenComment"] input[name="content"]', function (e) {
            if (e.keyCode === 13) {
                const btn = $(this).closest('form').find('.btn-daedaek-ok')[0];
                childrenCommentWriteOk(btn);
                $(this).val(''); // 입력 후 내용 비우기
            }
        });
    }


    function childrenCommentWriteCancel(btn) {
        const $form = $(btn).closest('form[name="formChildrenComment"]');
        const contentElement = $form.find('input[name="content"]');
        contentElement.val('');
        alert("대댓글쓰기가 취소 되었습니다.");

    }

    function childrenCommentWriteOk(btn) {
        const $form = $(btn).closest('form[name="formChildrenComment"]');
        const contentElement = $form.find('input[name="content"]');
        const content = contentElement.val().trim();
        if (content === "") {
            alert("대댓글 내용을 입력하세요.");
            contentElement.focus();
            return;
        }
        const parentId = $form.find('input[name="parentId"]').val();

        // 대댓글 쓰기 로직
        axios.post('<%=ctxPath%>/api/comment', {
            boardId: boardId,
            content: content,
            parentCommentId: parentId
        }).then(response => {
            alert("대댓글 쓰기 성공");
            console.log(response);
            const childDiv = $('#childDiv' + parentId);
            const responseData = response.data.success.responseData;
            const createdAtShort = responseData.createdAt.split('.')[0]; // . 기준 앞부분만

            childDiv.append(`<tr><td>↪️&nbsp;\${content}</td><td>\${createdAtShort}</td></tr>`); // 대댓글 추가
            //goReadCommentList(boardId, 1); // 댓글 목록을 다시 불러오기
            $form.hide(); // 폼 숨기기
            contentElement.val(''); // 입력 후 내용 비우기
        }).catch(error => {
            console.error(error);
            alert("대댓글 쓰기 실패: " + error.response.data.error.customMessage);
        });
    }

    function deletePostByIdAndPassword() {
        const password = prompt("삭제할 글의 비밀번호를 입력하세요");
        if (!password)
            return;

        const isConfirm = confirm("정말로 삭제하시겠습니까?");
        if (!isConfirm) {
            alert("삭제를 취소합니다.");
            return;
        }


        axios.delete('<%=ctxPath%>/api/board/' + boardId, {
            data: password,
            headers: {'Content-Type': 'text/plain'}
        }).then((response) => {
            alert(response.data.success.message);
            const redirectUrl = '${returnUrl}'
            if (redirectUrl) {
                location.href = redirectUrl;
                return;
            }
            history.back();
        }).catch(error => {
            alert(error.response.data.error.customMessage);
        });

    }

    function goModifyPage() {
        const inputPassword = prompt("수정할 글의 비밀번호를 입력하세요");
        axios.get('<%=ctxPath%>/api/board/verify/' + boardId, {
            params: {
                postPassword: inputPassword
            }
        })
            .then((response) => {
                if (response.data.success.responseData) {
                    location.href = '<%=ctxPath%>/board/modify/' + boardId;
                } else {
                    alert("수정 권한이 없습니다.");
                }
            }).catch(error => {
            alert(error.response.data.error.customMessage);
        });
    }

    //삭제 이벤트리스너
    $(document).on('click', '.btnDeleteComment', function (e) {
        const $btn = $(this);


        if ($(this).text() === "취소") {
            const contentElement = $btn.parent().parent().children('td:nth-child(2)');
            const content = contentElement.data('origin');
            contentElement.html(content);
            $(this).prev().text("수정")
                .removeClass("btn-info")
                .addClass("btn-secondary");
            $(this).text("삭제")
                .removeClass("btn-danger")
                .addClass("btn-secondary");
            alert("수정이 취소 되었습니다.");
            return;
        }

        const commentId = $(this).data('comment-id');
        // 여기에 삭제 로직 추가
        const isConfirm = confirm("정말로 삭제하시겠습니까?");
        if (!isConfirm) return;
        axios.delete('<%=ctxPath%>/api/comment/' + commentId)
            .then(response => {
                alert("댓글 삭제 성공");
                //goReadCommentList(boardId, 1); // 댓글 목록을 다시 불러오기
                //다시불러오지않고 해당요소 제거
                $(this).closest('tr').remove(); // 해당 댓글 행 제거
            })
            .catch(error => {
                console.error(error);
                alert("댓글 삭제 실패: " + error.response.data.error.customMessage);
            });
        // 예시: deleteCommentById(commentId);
    });//삭제버튼 이벤트리스나

    function modifyCommentById(commentId, content, contentElement) {
        if (!content || content.trim() === "") {
            alert("수정할 내용을 입력하세요.");
            return;
        }
        // 여기에 수정 로직 추가
        axios.put('<%=ctxPath%>/api/comment/' + commentId, {
            content: content
        })
            .then(response => {
                alert("댓글 수정 성공");
                //goReadCommentList(boardId, 1); // 댓글 목록을 다시 불러오기
                contentElement.html(content);
            })
            .catch(error => {
                console.error(error);
                alert("댓글 수정 실패: " + error.response.data.error.customMessage);
            });
    }

    $(document).on('click', '.btnModifyComment', function (e) {
        const $btn = $(this);
        const commentId = $btn.data('comment-id');

        const contentElement = $btn.parent().parent().children('td:nth-child(2)');
        const content = contentElement.text();

        if ($btn.text() === "수정") {
            //alert('수정할 commentId: ' + commentId);
            $btn.text("완료")
                .removeClass("btn-secondary")
                .addClass("btn-info");
            $btn.next()
                .text("취소")
                .removeClass("btn-secondary")
                .addClass("btn-danger");
            // input 태그로 변경되기 전에 이전 값을 data로 저장
            contentElement.data('origin', content);
            //댓글을 수정할수있도록 input 태그로 변경
            contentElement.html(`<input type='text' id="commentUpdate\${commentId}" class='form-control' size="40" value='\${content}'/>`);

            $(document).on("keyup", "#commentUpdate" + commentId, function (e) {
                if (e.keyCode !== 13) return;
                modifyCommentById(commentId, $(this).val(), contentElement);
            })

            return;
        }
        if ($btn.text() === "완료") {
            modifyCommentById(commentId, contentElement.children('input').val(), contentElement);
        }

        // 예시: modifyCommentById(commentId);

    })

    function goAnswerPage() {
        const requestRootId = rootBoardId ? rootBoardId : boardId;

        location.href = `<%=ctxPath%>/board/write/\${requestRootId}/\${boardId}`;
    }
</script>
<div style="display: flex;">
    <div style="width: 80%; margin: auto; padding-left: 3%;">

        <h2 style="margin-bottom: 30px;">글내용보기</h2>
        <c:if test="${not empty requestScope.board}">
            <table class="table table-bordered table-dark"
                   style="width: 100%; word-wrap: break-word; table-layout: fixed;">
                <tr>
                    <th style="width: 15%">글번호</th>
                    <td>${requestScope.board.boardId}</td>
                </tr>

                <tr>
                    <th>성명</th>
                    <td>${requestScope.board.userInfo.username}</td>
                </tr>

                <tr>
                    <th>제목</th>
                    <td>${requestScope.board.title}</td>
                </tr>

                <tr>
                    <th>내용</th>
                    <td>
                        <p style="word-break: break-all;">
                                ${requestScope.board.content}
                                <%--
                                style="word-break: break-all; 은 공백없는 긴영문일 경우 width 크기를 뚫고 나오는 것을 막는 것임.
                                    그런데 style="word-break: break-all; 나 style="word-wrap: break-word; 은
                                    테이블태그의 <td>태그에는 안되고 <p> 나 <div> 태그안에서 적용되어지므로 <td>태그에서 적용하려면
                                <table>태그속에 style="word-wrap: break-word; table-layout: fixed;" 을 주면 된다.
                             --%>
                        </p>
                    </td>
                </tr>

                <tr>
                    <th>조회수</th>
                    <td>${board.viewCount}</td>
                </tr>

                <tr>
                    <th>날짜</th>
                    <td>${board.createdAt}</td>
                </tr>
                <tr>
                    <th>첨부파일</th>
                    <c:choose>

                        <c:when test="${not empty board.fileName}">
                            <c:if test="${sessionScope.loginuser != null}">
                                <td>
                                    <a href="<%=ctxPath%>/api/storage/download?filePath=${board.filePath}&fileName=${board.fileName}">${board.fileName}</a>
                                </td>
                            </c:if>
                            <c:if test="${sessionScope.loginuser == null}">
                                <td>
                                    <a style="cursor: pointer; color: #1e74ef" onclick="alert('로그인 후 다운로드 가능합니다.'); return false;">${board.fileName}</a>
                                </td>
                            </c:if>
                        </c:when>
                        <c:otherwise>
                            <td>첨부파일이 없습니다</td>
                        </c:otherwise>

                    </c:choose>

                </tr>
            </table>

        </c:if>
        <c:if test="${empty board}">
            <div style="padding: 20px 0; font-size: 16pt; color: red;">존재하지 않습니다</div>
        </c:if>

        <div class="mt-5">
            <%--이전글제목과 다음글 제목 보기 시작  --%>
            <div style="margin-bottom: 1%">
                이전글&nbsp;제목&nbsp;&nbsp;<span class="move"
                                             onclick="goView(${board.boardPrevNextValue.prevBoardId})">${board.boardPrevNextValue.refinedPrevTitle}</span>
            </div>
            <div style="margin-bottom: 1%">
                다음글&nbsp;제목&nbsp;&nbsp;<span class="move"
                                             onclick="goView(${board.boardPrevNextValue.nextBoardId})">${board.boardPrevNextValue.refinedNextTitle}</span>
            </div>
            <button type="button" class="btn btn-secondary" onclick="location.href='<%=ctxPath%>/board/list/season2'">
                전체목록보기
            </button>
            <c:if test="${not empty returnUrl}">
                <button type="button" class="btn btn-secondary" onclick="location.href='${returnUrl}'">검색된결과목록보기
                </button>
            </c:if>
            <c:if test="${not empty sessionScope.loginuser && sessionScope.loginuser.userId == board.userInfo.userId}">
                <button type="button" class="btn btn-secondary" onclick="goModifyPage()">수정하기
                </button>
                <button type="button" class="btn btn-secondary" onclick="deletePostByIdAndPassword()">삭제하기
                </button>
            </c:if>
            <c:if test="${not empty sessionScope.loginuser && sessionScope.loginuser.role.value == '운영자'}">
                <button type="button" class="btn btn-secondary" onclick="goAnswerPage()">답변달기
                </button>
            </c:if>

            <%--                <button type="button" class="btn btn-secondary" onclick="history.back()">검색된결과목록보기</button>--%>

            <%--이전글제목과 다음글 제목 보기 끝 --%>
            <%--            댓글쓰기 시작--%>
            <c:if test="${not empty sessionScope.loginuser}">
                <form name="addWriteFrm" id="addWriteFrm" style="margin-top: 20px;">
                    <table class="table" style="width: 100%;">
                        <tr style="height: 30px;">
                            <th width="10%">성명</th>
                            <td>
                                <input type="hidden" name="fk_userid" value="${sessionScope.loginuser.userId}"
                                       readonly/>
                                <input type="text" name="name" value="${sessionScope.loginuser.username}" readonly/>
                            </td>
                        </tr>
                        <tr style="height: 30px;">
                            <th>댓글내용</th>
                            <td>
                                <input type="text" name="content" maxlength="1000" size="100"/>
                                    <%--                                원 게시물 번호--%>
                                <input type="hidden" name="boardId" value="${board.boardId}" readonly/>
                            </td>
                        </tr>
                            <%--                        댓글 쓰기에 파일 첨부하기 --%>
                        <tr style="height: 30px;">
                            <th>파일첨부</th>
                            <td>
                                <input type="file" name="file"/>
                            </td>
                        </tr>
                        <tr>
                            <th colspan="2">
                                <button type="button" class="btn btn-success btn-sm mr-3" onclick="goAddWrite()">확인
                                </button>
                                <button type="reset" class="btn btn-success btn-sm">취소</button>
                            </th>
                        </tr>
                    </table>
                </form>
            </c:if>
            <%--            댓글 내용 보여주기--%>
            <h3 style="margin-top: 50px;">댓글내용</h3>
            <table class="table" style="width: 100%; margin-top: 2%; margin-bottom: 3%;">
                <thead>
                <tr>
                    <th style="width: 6%;">순번</th>
                    <th style="width: 36%; text-align: center;">내용</th>
                    <th style="width:  8%; text-align: center;">답글보기</th>
                    <%--                    댓글쓰기에 첨부파일이 있는경우--%>
                    <th style="width: 10%;">첨부파일</th>
                    <th style="width: 8%;">bytes</th>
                    <%--                    댓글쓰기에 첨부파일이 있는경우 끝--%>
                    <th style="width:  8%; text-align: center;">작성자</th>
                    <th style="width: 12%; text-align: center;">작성일자</th>
                    <th style="width: 12%; text-align: center;">수정/삭제</th>
                </tr>
                </thead>
                <tbody id="commentDisplay">

                </tbody>
            </table>
            <%--                    댓글 페이지바가 보여지는 곳--%>
            <div style="display: flex; margin-bottom: 50px;">
                <div id="pageBar" style="margin: auto; text-align: center;"></div>
            </div>


        </div>

    </div>
</div>
<form name="goViewForm">
    <input type="hidden" name="boardId"/>
</form>

<jsp:include page="../../footer/footer1.jsp"/>