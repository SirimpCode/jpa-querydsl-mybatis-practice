<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
  String ctxPath = request.getContextPath();
  //     /myspring
%>

<jsp:include page="../../header/header1.jsp"/>
<style type="text/css">

  th {
    background-color: #ddd
  }

  .subjectStyle {
    font-weight: bold;
    color: navy;
    cursor: pointer;
  }

  a {
    text-decoration: none !important;
  }

  /* 페이지바의 a 태그에 밑줄 없애기 */
  /*    페이지바 css*/
  .page-btn {
    padding: 6px 12px;
    border: 1px solid #ccc;
    background-color: white;
    color: black;
    border-radius: 5px;
    text-decoration: none;
    transition: all 0.2s ease;
  }

  .page-btn:hover {
    background-color: #f0f0f0;
    color: navy;
  }

  .page-current {
    padding: 6px 12px;
    border: 1px solid navy;
    background-color: navy;
    color: white;
    border-radius: 5px;
    font-weight: bold;
  }





</style>
<script>
  //파라미터를 찾아오기
  const params = new URLSearchParams(window.location.search);
  const searchWord = params.get('searchWord');
  const searchType = params.get('searchType');
  const searchSort = params.get('searchSort');
  const page = params.get('page')? params.get('page') : 1; // 페이지가 없으면 1페이지로 설정
  const size = params.get('size')? params.get('size') : 10; // 사이즈가 없으면 10개로 설정


  // JSP의 JSON 문자열을 JS에서 사용 가능하게 할당
  let boardList = null;

  const fetchBoardList = async () => {
    try {
      const response = await axios.get('<%=ctxPath%>/api/board/list', {
        params: {
          searchWord: searchWord,
          searchType: searchType,
          searchSort: searchSort,
          page: page,
          size: size
        }
      });
      return response.data;
    } catch (error) {
      console.error("Error fetching board list:", error);
      return { elements: [], totalElements: 0, totalPages: 0, hasNext: false };
    }
  };
  document.addEventListener("DOMContentLoaded", async () => {
    boardList = await fetchBoardList();
    console.log(boardList); // 여기서 데이터 확인
    // 이후 boardList 활용
    const elements = boardList.success.responseData.elements;

    const tbody = document.getElementById("boardTableBody");
    const startCount = (page - 1) * size + 1;
    tbody.innerHTML = renderBoardRows(elements, 0, {count: startCount });
  });

  /**
   * ✅ 재귀적으로 게시글 렌더링
   * @param {Array} boards - 게시글 목록 (혹은 자식)
   * @param {number} depth - 들여쓰기 깊이
   * @param {object} countObj - 카운터 객체
   */
  function renderBoardRows(boards, depth, countObj = { count: 1 }) {
    let html = "";

    boards.forEach(board => {
      const indent = depth * 20; // 들여쓰기 px

      html += `
        <tr>
          <td>\${countObj.count++}</td>
          <td>\${board.boardId}</td>
          <td>
            <span
              class="subject"
              onclick="goView(\${board.boardId})"
              style="cursor:pointer; display:inline-block; padding-left:\${indent}px;"
            >
              \${board.refinedTitle || board.title}
              \${board.fileAttached ? '📋' : ''}
              \${board.commentCount > 0
                ? `<span class="ms-1 badge bg-danger text-white">\${board.commentCount}</span>`
                : ""}
            </span>
          </td>
          <td>\${board.writer || ""}</td>
          <td>\${formatDate(board.createdAt)}</td>
          <td>\${board.viewCount}</td>
        </tr>
      `;

      if (board.childrenBoards && board.childrenBoards.length > 0) {
        html += renderBoardRows(board.childrenBoards, depth + 1, countObj);
      }
    });

    return html;
  }

  /** ✅ 날짜 포맷팅 함수 (YYYY-MM-DD) */
  function formatDate(isoDateStr) {
    if (!isoDateStr) return '';
    const date = new Date(isoDateStr);
    return date.toISOString().slice(0, 10);
  }







  $(() => {
    //파라미터를 찾아서 검색폼태그의 초기값 설정
    if (searchWord) {
      $('input[name="searchWord"]').val(searchWord);
    }
    $('select[name="searchType"]').val("title"); // 검색타입의 초기값은 글제목으로 설정
    if (searchType) {
      $('select[name="searchType"]').val(searchType);
    }//초기 세팅 끝
    $('span.subject').hover(function (e) {
      $(e.target).addClass('subjectStyle');
    }, function (e) {
      $(e.target).removeClass('subjectStyle');
    });
    //     검색어 자동와성하기
    $('#displayList').hide();
    $('input[name="searchWord"]').keyup(function (e) {

      const wordLength = $(this).val().trim().length;
      if (wordLength === 0) {
        $('#displayList').hide();
      } else {
        // 엔터일경우
        //또는
        if (e.keyCode === 13) {
          // 검색어로 글목록 페이지로 이동
          goSearch();
          return;
        }
        $.ajax({
          url: "<%= ctxPath%>/api/board/search-helper",
          type: "get",
          data: {
            "searchType": $('select[name="searchType"]').val(),
            "searchWord": $('input[name="searchWord"]').val()
          },
          dataType: "json",
          success: function (json) {
            console.log(json);
            if(json.success.responseData)
              searchTermsLogic(json.success.responseData);
            // console.log(JSON.stringify(json.success.responseData));
          },
          error: function (request, status, error) {
            alert("code: " + request.status + "\n" + "message: " + request.responseText + "\n" + "error: " + error);
          }

        });

      }
    })//키업이벤트 끗


    $(document).on('click', 'span.result', function (e) {
      // 검색어 자동완성에서 클릭했을 때
      const word = $(e.target).text();
      console.log(word);
      // 글번호로 글보기 페이지로 이동
      $('input[name="searchWord"]').val(word);
      $('#displayList').hide(); // 검색어 자동완성 목록 숨기기
    });



  });//돔 불러온후 실행되는 메서드 끄읕
  function goSearch() {
    // 검색어로 글목록 페이지로 이동
    const form = document.searchFrm;
    form.action = '<%=ctxPath%>/board/list/season2';
    form.method = 'get';
    // 검색어가 없으면 검색어를 입력하라고 경고창 띄우기
    if ($('input[name="searchWord"]').val().trim() === "") {
      alert("검색어를 입력하세요.");
      $('input[name="searchWord"]').focus();
      return;
    }
    // 검색어가 있으면 글목록 페이지로 이동


    // 검색어로 글목록 페이지로 이동
    form.submit();
  }

  function searchTermsLogic(wordArr) {
    let html = "";
    $.each(wordArr, function (i, word) {
      console.log(word);

      // word.toLowerCase(); // 은 word 를 모두 소문자로 변경
      const idx = word.toLowerCase().indexOf( $('input[name="searchWord"]').val().toLowerCase() );

      // 만약에 검색어가 JaVA 라면 java 로 변경해서 검색어와 비교한다.
      const len = $('input[name="searchWord"]').val().length;
      // 검색어의 길이 len 을 알아와 검색어와 중복되는 글자 색칠하기
      /*console.log(word.substring(0, idx));
      console.log(word.substring(idx, idx+len));
      console.log(word.substring(idx+len));*/
      const result = word.substring(0, idx) + "<span style='color: red;'>" +
              word.substring(idx, idx + len) + "</span>" +
              word.substring(idx + len);
      console.log(result);
      html += `<span style="cursor:pointer;" class="result">\${result}</span><br>`;
    })
    const wordWidthValue = $('input[name="searchWord"]').css('width');// 검색어 input 태그 width값 알아오기

    $('#displayList').css({"width":wordWidthValue}).html(html).show();
  }

  function goView(boardId) {
    // 글번호로 글보기 페이지로 이동
    const form = document.goViewForm;
    let actionUrl = '<%=ctxPath%>/board/view/'+ boardId;
    //파라미터를 찾아서 파라미터로 같이 넘기기
    let queryArr = [];
    if (searchWord) queryArr.push('searchWord=' + encodeURIComponent(searchWord));
    if (searchType) queryArr.push('searchType=' + encodeURIComponent(searchType));
    if (searchSort) queryArr.push('searchSort=' + encodeURIComponent(searchSort));
    queryArr.push('returnUrl=' + encodeURIComponent(window.location.pathname + window.location.search));

    if (queryArr.length > 0) actionUrl += '?' + queryArr.join('&');

    form.action = actionUrl;
    form.method = 'post';
    form.boardId.value = boardId;
    // 글번호로 글보기 페이지로 이동
    form.submit();
  }

</script>

<c:set var="currentPage" value="${empty param.page ? 1 : param.page}" />
<c:set var="pageSize" value="${empty param.size ? 10 : param.size}" />
<c:set var="searchType" value="${empty param.searchType ? '' : param.searchType}" />
<c:set var="searchWord" value="${empty param.searchWord ? '' : param.searchWord}" />
<c:set var="searchSort" value="${empty param.searchSort ? '' : param.searchSort}" />
<c:set var="queryParams" value=""/>
<c:if test="${not empty param.searchType}">
  <c:set var="queryParams" value="${queryParams}searchType=${param.searchType}&"/>
</c:if>
<c:if test="${not empty param.searchWord}">
  <c:set var="queryParams" value="${queryParams}searchWord=${param.searchWord}&"/>
</c:if>
<c:if test="${not empty param.searchSort}">
  <c:set var="queryParams" value="${queryParams}searchSort=${param.searchSort}&"/>
</c:if>


<c:set var="pageBlockSize" value="5" />
<c:set var="startPage" value="${currentPage - (pageBlockSize / 2) < 1 ? 1 : currentPage - (pageBlockSize / 2 - 1)}" />
<c:set var="endPage" value="${startPage + pageBlockSize - 1 > boardList.totalPages ? boardList.totalPages : startPage + pageBlockSize - 1}" />
<c:if test="${endPage - startPage < pageBlockSize - 1}">
  <c:set var="startPage" value="${endPage - pageBlockSize + 1 < 1 ? 1 : endPage - pageBlockSize + 1}" />
</c:if>



<div style="display: flex;">
  <div style="width: 80%; margin: auto; padding-left: 3%;">

    <h2 style="margin-bottom: 30px;">글목록</h2>

    <!-- ✅ Bootstrap Table Layout -->
    <table class="table table-bordered table-hover">
      <thead class="table-light">
      <tr>
        <th>#</th>
        <th>ID</th>
        <th>제목</th>
        <th>작성자</th>
        <th>작성일</th>
        <th>조회수</th>
      </tr>
      </thead>
      <tbody id="boardTableBody">
      <!-- 여기에 JS로 동적 렌더링 -->
      </tbody>
    </table>

    <%--         글검색 폼 추가하기--%>
    <form name="searchFrm" style="margin-top: 20px;">
      <select name="searchType" style="height: 26px;">
        <option value="title">글제목</option>
        <option value="content">글내용</option>
        <option value="all">글제목+글내용</option>
        <option value="writer">글쓴이</option>
      </select>
      <input type="text" name="searchWord" size="50" autocomplete="off"/>
      <input type="text"
             style="display: none;"/> <%-- form 태그내에 input 태그가 오로지 1개 뿐일경우에는 엔터를 했을 경우 검색이 되어지므로 이것을 방지하고자 만든것이다. --%>
      <button type="button" class="btn btn-secondary btn-sm" onclick="goSearch()">검색</button>
    </form>
    <%-- 검색자동완성--%>
    <div id="displayList"
         style="border:solid 1px gray; border-top:0px; height:100px; margin-left:8.7%; margin-top:-1px; margin-bottom:30px; overflow:auto;">

    </div>
  </div>
</div>
<form name="goViewForm">
  <input type="hidden" name="boardId"/>
</form>

<c:if test="${boardList.totalPages > 0}">

  <div style="margin-top: 30px; text-align: center;">
    <ul style="display: inline-flex; list-style: none; padding: 0; gap: 5px;">

        <%-- 맨 처음 --%>
      <li><a class="page-btn" href="?${queryParams}page=1&size=${pageSize}">«</a></li>

        <%-- 이전 --%>
      <c:if test="${currentPage > 1}">
        <li><a class="page-btn" href="?${queryParams}page=${currentPage - 1}&size=${pageSize}">‹</a></li>
      </c:if>

        <%-- 페이지 번호들 --%>
      <c:forEach var="i" begin="${startPage}" end="${endPage}">
        <li>
          <c:choose>
            <c:when test="${i == currentPage}">
              <span class="page-current">${i}</span>
            </c:when>
            <c:otherwise>
              <a class="page-btn" href="?${queryParams}page=${i}&size=${pageSize}">${i}</a>
            </c:otherwise>
          </c:choose>
        </li>
      </c:forEach>

        <%-- 다음 --%>
      <c:if test="${boardList.hasNext}">
        <li><a class="page-btn" href="?${queryParams}page=${currentPage + 1}&size=${pageSize}">›</a></li>
      </c:if>

        <%-- 마지막 --%>
      <li><a class="page-btn" href="?${queryParams}page=${boardList.totalPages}&size=${pageSize}">»</a></li>
    </ul>

      <%-- 총 게시글, 현재 페이지 표시 --%>
    <div style="margin-top: 10px; font-size: 14px; color: gray;">
      총 <strong>${boardList.totalElements}</strong>개 글,
      <strong>${boardList.totalPages}</strong>페이지 중
      <strong>${currentPage}</strong>페이지
    </div>
  </div>
</c:if>




<script>
  document.addEventListener("DOMContentLoaded", function () {

  });
</script>
<jsp:include page="../../footer/footer1.jsp"/>