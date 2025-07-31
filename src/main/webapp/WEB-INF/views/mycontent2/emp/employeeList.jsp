<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
    String ctxPath = request.getContextPath();
    //     /myspring
%>

<jsp:include page="../../header/header2.jsp"/>
<style type="text/css">

    table#emptbl {
        width: 100%;
    }

    table#emptbl th, table#emptbl td {
        border: solid 1px gray;
        border-collapse: collapse;
    }

    table#emptbl th {
        text-align: center;
        background-color: #ccc;
    }

</style>
<script>
    $(() => {
        const searchFrm = document.searchFrm;
        const btnSearch = document.getElementById('btnSearch');
        btnSearch.addEventListener('click', () => {
            const deptIds = getCheckedDeptIds();
            const gender = searchFrm.gender.value;
            const searchParams = createRequestParam(deptIds, gender);
            requestSearch(searchParams);

        })
        function getCheckedDeptIds() {
            const deptIds = [];
            const checkboxes = searchFrm.deptId;
            if (checkboxes.length) {
                for (let i = 0; i < checkboxes.length; i++) {
                    if (checkboxes[i].checked) {
                        deptIds.push(checkboxes[i].value);
                    }
                }
            } else {
                if (checkboxes.checked) {
                    deptIds.push(checkboxes.value);
                }
            }
            return deptIds;
        }

        const btnExcel = document.getElementById('btnExcel');
        btnExcel.addEventListener('click', () => {

            const deptIds = getCheckedDeptIds();
            const gender = searchFrm.gender.value;
            const searchParams = createRequestParam(deptIds, gender);


            requestDownloadExcel(searchParams);
        });
        function requestDownloadExcel(searchParams) {
            const form = document.createElement('form');
            form.method = 'post';
            form.action = '<%=ctxPath%>/api/emp/excel-download';
            form.style.display = 'none';

            for (const [key, value] of searchParams.entries()) {
                const input = document.createElement('input');
                input.type = 'hidden';
                input.name = key;
                input.value = value;
                form.appendChild(input);
            }
            document.body.appendChild(form);
            form.submit();
            document.body.removeChild(form);
        }

        const btnUploadExcel = document.getElementById('btnUploadExcel');
        btnUploadExcel.addEventListener('click', () => {
            const uploadInput = document.getElementById('upload_excel_file');
            if (uploadInput.files.length === 0) {
                alert("업로드할 엑셀 파일을 선택해주세요.");
                return;
            }

            const formData = new FormData();
            formData.append('file', uploadInput.files[0]);

            axios.post('<%=ctxPath%>/api/emp/excel-upload', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            }).then(response => {
                console.log(response);
                alert(response.data.success.message);
                requestSearch(createRequestParam([], '')); // 업로드 후 전체 사원 목록 조회
            }).catch(error => {
                console.error(error);
                console.error(error.response.data.error.systemMessage);
                alert(error.response.data.error.customMessage);
            });
        });

    });//돔 로드후 실행함수 끗
    function createRequestParam(deptIds, gender){
        const searchParams = new URLSearchParams();
        if (deptIds.length > 0)
            searchParams.append('departmentIds', deptIds);
        if (gender)
            searchParams.append('gender', gender);
        return searchParams;
    }



    function requestSearch(searchParams) {

        axios.get('<%=ctxPath%>/api/emp/list', {
            params: searchParams
        }).then((response) => {
            const empList = response.data.success.responseData.empList;
            console.log(empList);
            viewEmpList(empList);
        })
            .catch(error => {
                console.error("사원정보 조회 실패:", error);
                alert("사원정보 조회 실패: " + error.response.data.error.customMessage);
            })
    }

    function viewEmpList(empList){

        const empListTbody = document.getElementById('empListTbody');
        empListTbody.innerHTML = ''; // 기존 내용 초기화

        empList.forEach(emp => {
            const tr = document.createElement('tr');

            const tdDeptId = document.createElement('td');
            tdDeptId.style.textAlign = 'center';
            tdDeptId.textContent = emp.departmentId != null ? emp.departmentId : '';
            tr.appendChild(tdDeptId);

            const tdDeptName = document.createElement('td');
            tdDeptName.textContent = emp.departmentName != null ? emp.departmentName : '';
            tr.appendChild(tdDeptName);

            const tdEmpId = document.createElement('td');
            tdEmpId.style.textAlign = 'center';
            tdEmpId.textContent = emp.employeeId;
            tr.appendChild(tdEmpId);

            const tdName = document.createElement('td');
            tdName.textContent = `\${emp.firstName} \${emp.lastName}`;
            tr.appendChild(tdName);

            const tdHireDate = document.createElement('td');
            tdHireDate.style.textAlign = 'center';
            tdHireDate.textContent = emp.refinedHireDate;
            tr.appendChild(tdHireDate);

            const tdSalary = document.createElement('td');
            tdSalary.style.textAlign = 'right';
            tdSalary.textContent = emp.refinedSalary;
            tr.appendChild(tdSalary);

            const tdGender = document.createElement('td');
            tdGender.style.textAlign = 'center';
            tdGender.textContent = emp.gender;
            tr.appendChild(tdGender);

            const tdAge = document.createElement('td');
            tdAge.style.textAlign = 'center';
            tdAge.textContent = emp.age;
            tr.appendChild(tdAge);

            empListTbody.appendChild(tr);
        });
    }
</script>
<div style="display: flex; margin-bottom: 50px;">
    <div style="width: 80%; min-height: 1100px; margin:auto; ">

        <h2 style="margin: 50px 0;">HR 사원정보 조회하기</h2>

        <form name="searchFrm">
            <c:if test="${not empty empListWrap.departmentIds}">
                <span style="display: inline-block; width: 150px; font-weight: bold;">부서번호선택</span>
                <c:forEach var="deptId" items="${empListWrap.departmentIds}" varStatus="status">

                    <label for="${status.index}" style="cursor: pointer">
                        <c:if test="${deptId != null}">
                            ${deptId}
                        </c:if>
                        <c:if test="${deptId == null}">
                            부서 없음
                        </c:if>
                    </label>
                    <input id="${status.index}" type="checkbox" name="deptId" value="${deptId != null ? deptId : 0}"/>&nbsp;&nbsp;
                </c:forEach>
            </c:if>
            <input type="text" name="deptIdStr"/>

            <select name="gender" style="height: 30px; width: 120px; margin: 10px 30px 0 0;">
                <option value="">성별선택</option>
                <option value="남자">남성</option>
                <option value="여자">여성</option>
            </select>
            <button type="button" class="btn btn-secondary btn-sm" id="btnSearch">검색하기</button>
            &nbsp;&nbsp;
            <button type="button" class="btn btn-success btn-sm" id="btnExcel">Excel 파일로 저장</button>

        </form>

        <br>

<%--        엑셀관련파일 업로드 하기 시작 ==== --%>
        <form style="margin-bottom: 10px;" name="excel_upload_frm" method="post" enctype="multipart/form-data" >
            <input type="file" id="upload_excel_file" name="excel_file" accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel" />
            <br>
            <button type="button" class="btn btn-info btn-sm" id="btnUploadExcel" style="margin-top: 1%;">Excel 파일 업로드 하기</button>
        </form>
        <!-- ==== 엑셀관련파일 업로드 하기 끝 ==== -->


        <table id="emptbl">
            <thead>
            <tr>
                <th>부서번호</th>
                <th>부서명</th>
                <th>사원번호</th>
                <th>사원명</th>
                <th>입사일자</th>
                <th>월급</th>
                <th>성별</th>
                <th>나이</th>
            </tr>
            </thead>
            <tbody id="empListTbody">
            <c:forEach items="${empListWrap.empList}" var="emp">
                <tr>
                    <td style="text-align: center">${emp.departmentId}</td>
                    <td>${emp.departmentName}</td>
                    <td style="text-align: center">${emp.employeeId}</td>
                    <td>${emp.firstName} ${emp.lastName}</td>
                    <td style="text-align: center">${emp.refinedHireDate}</td>
                    <td style="text-align: right">${emp.refinedSalary}</td>
                    <td style="text-align: center">${emp.gender}</td>
                    <td style="text-align: center">${emp.age}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

    </div>
</div>


<jsp:include page="../../footer/footer2.jsp"/>