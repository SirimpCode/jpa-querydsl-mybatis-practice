<%--
  Created by IntelliJ IDEA.
  User: user
  Date: 25. 7. 30.
  Time: 오전 9:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String ctxPath = request.getContextPath();
    //     /myspring
%>

<jsp:include page="../../header/header2.jsp"/>

<style type="text/css">
    .highcharts-figure,
    .highcharts-data-table table {
        min-width: 320px;
        max-width: 800px;
        margin: 1em auto;
    }

    div#chart_container {
        height: 400px;
    }

    .highcharts-data-table table {
        font-family: Verdana, sans-serif;
        border-collapse: collapse;
        border: 1px solid #ebebeb;
        margin: 10px auto;
        text-align: center;
        width: 100%;
        max-width: 500px;
    }

    .highcharts-data-table caption {
        padding: 1em 0;
        font-size: 1.2em;
        color: #555;
    }

    .highcharts-data-table th {
        font-weight: 600;
        padding: 0.5em;
    }

    .highcharts-data-table td,
    .highcharts-data-table th,
    .highcharts-data-table caption {
        padding: 0.5em;
    }

    .highcharts-data-table thead tr,
    .highcharts-data-table tr:nth-child(even) {
        background: #f8f8f8;
    }

    .highcharts-data-table tr:hover {
        background: #f1f7ff;
    }

    input[type="number"] {
        min-width: 50px;
    }

    div#table_container table {
        width: 100%
    }

    div#table_container th, div#table_container td {
        border: solid 1px gray;
        text-align: center;
    }

    div#table_container th {
        background-color: #595959;
        color: white;
    }
</style>

<script src="<%= ctxPath%>/Highcharts-10.3.1/code/highcharts.js"></script>
<script src="<%= ctxPath%>/Highcharts-10.3.1/code/modules/exporting.js"></script>
<script src="<%= ctxPath%>/Highcharts-10.3.1/code/modules/export-data.js"></script>
<script src="<%= ctxPath%>/Highcharts-10.3.1/code/modules/accessibility.js"></script>

<script src="<%= ctxPath%>/Highcharts-10.3.1/code/modules/series-label.js"></script>

<script src="<%= ctxPath%>/Highcharts-10.3.1/code/modules/data.js"></script>
<script src="<%= ctxPath%>/Highcharts-10.3.1/code/modules/drilldown.js"></script>


<div style="display: flex;">
    <div style="width: 80%; min-height: 1100px; margin:auto; ">

        <h2 style="margin: 50px 0;">HR 사원 통계정보(차트)</h2>

        <form name="searchFrm" style="margin: 20px 0 50px 0; ">
            <select name="searchType" id="searchType" style="height: 30px;">
                <option value="">통계선택하세요</option>
                <option value="deptname">부서별 인원통계</option>
                <option value="gender">성별 인원통계</option>
                <option value="genderHireYear">성별 입사년도별 통계</option>
                <option value="deptnameGender">부서별 성별 인원통계</option>
                <option value="pageurlUsername">페이지별 사용자별 접속통계</option>
            </select>
        </form>

        <div id="chart_container"></div>
        <div id="table_container" style="margin: 40px 0 0 0;"></div>

    </div>
</div>

<script type="text/javascript">
    // 돔 로드후 실행 함수
    document.addEventListener("DOMContentLoaded", () => {
        const searchType = document.getElementById("searchType");
        searchType.addEventListener("change", (e) => {
            const searchTypeVal = e.target.value;
            // $(e.target).val() 은
            // "" 또는 "deptname" 또는 "gender" 또는 "genderHireYear" 또는 "deptnameGender" 또는 "pageurlUsername" 이다.
            if (searchTypeVal) {
                funcChoice(searchTypeVal);
            } else {
                resetChartContainer()
            }
        });
    }); // 페이지가 로드된 후 실행 끗

    function createPageUrlUsername(pageUrlUserNameResponse) {
        console.log(pageUrlUserNameResponse);
        // 유저명 추출
        const userNames = [...new Set(pageUrlUserNameResponse.data.map(item => item.name))];
// 페이지명 추출
        const pageNames = [...new Set(pageUrlUserNameResponse.data.map(item => item.deptName))];

// 유저별 시리즈 생성
        const series = userNames.map(user => ({
            name: user,
            data: pageNames.map(page =>
                // 해당 유저가 해당 페이지에 방문한 횟수(y), 없으면 0
                (pageUrlUserNameResponse.data.find(item => item.name === user && item.deptName === page)?.y) || 0
            )
        }));
        resetChartContainer();

        Highcharts.chart('chart_container', {
            chart: { type: 'bar' },
            title: { text: pageUrlUserNameResponse.label },
            subtitle: {
                text: `Source: <a target="_blank">${pageUrlUserNameResponse.thead}</a>`
            },
            xAxis: {
                categories: pageNames,
                title: { text: null }
            },
            yAxis: {
                min: 0,
                title: { text: '방문 횟수', align: 'high' },
                labels: { overflow: 'justify' }
            },
            tooltip: { valueSuffix: '회' },
            plotOptions: {
                bar: { dataLabels: { enabled: true } }
            },
            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'top',
                x: -40,
                y: 80,
                floating: true,
                borderWidth: 1,
                backgroundColor: Highcharts.defaultOptions.legend.backgroundColor || '#FFFFFF',
                shadow: true
            },
            credits: { enabled: false },
            series: series
        });
    }

    // 차트 생성 함수
    async function funcChoice(searchTypeVal) {

        switch (searchTypeVal) {
            case "pageurlUsername"://basic bar 차트
                alert("페이지별 사용자별 접속통계 차트 생성");
                const pageUrlUserNameResponse = await requestPieChartData(searchTypeVal);
                createPageUrlUsername(pageUrlUserNameResponse);
                break;
            case "deptnameGender":
                alert("부서별 성별 인원통계 차트 생성");
                const deptGenderChartResponse = await requestPieChartData(searchTypeVal);
                console.log(deptGenderChartResponse);
                createColumnWithDrilldown(deptGenderChartResponse);//column  with drilldown 차트
                break;
            case "genderHireYear":
                alert("성별 입사년도별 통계 차트 생성");
                const genderHireYearChartResponse = await requestPieChartData(searchTypeVal);
                createBasicLineChart(genderHireYearChartResponse);// line 차트
                break;
            case "gender": // pie 차트
                const genderChartDataList = await requestPieChartData(searchTypeVal);
                createPieChart(genderChartDataList);

                break;
            case "deptname": // pie 차트
                const chartDataList = await requestPieChartData(searchTypeVal);
                createPieChart(chartDataList);
                break;

        }
    }

    function createColumnWithDrilldown(deptGenderChartResponse) {
        resetChartContainer();

        // deptGenderChartResponse.data를 부서별로 그룹화
        const deptDrillDown = {};
        deptGenderChartResponse.data.forEach(item => {
            if (!deptDrillDown[item.deptName]) deptDrillDown[item.deptName] = [];
            deptDrillDown[item.deptName].push([item.name + ' ' + item.count+'명', item.deptGenderPercent]);
        });

// drilldown.series 생성
        const drilldownSeries = Object.entries(deptDrillDown).map(([deptName, dataArr]) => ({
            name: deptName,
            id: deptName,
            data: dataArr
        }));
        Highcharts.chart('chart_container', {
            chart: {
                type: 'column'
            },
            title: {
                align: 'left',
                text: deptGenderChartResponse.label
            },
            subtitle: {
                align: 'left',
                text: 'Click the columns to view versions. Source: <a href="http://statcounter.com" target="_blank">statcounter.com</a>'
            },
            accessibility: {
                announceNewData: {
                    enabled: true
                }
            },
            xAxis: {
                type: 'category'
            },
            yAxis: {
                title: {
                    text: 'Total percent market share'
                }

            },
            legend: {
                enabled: false
            },
            plotOptions: {
                series: {
                    borderWidth: 0,
                    dataLabels: {
                        enabled: true,
                        format: '{point.y:.1f}%'
                    }
                }
            },

            tooltip: {
                headerFormat: '<span style="font-size:11px">{series.name}</span><br>',
                pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y:.2f}%</b> of total<br/>'
            },

            series: [
                {
                    name: deptGenderChartResponse.label,
                    colorByPoint: true,
                    data: deptGenderChartResponse.data.map(item => ({
                        name: item.deptName+' '+item.name,
                        y: item.y,
                        drilldown : item.deptName
                    }))
                }
            ],
            drilldown: {
                breadcrumbs: {
                    position: {
                        align: 'right'
                    }
                },
                series: drilldownSeries
            }
        });


    }


    function requestPieChartData(searchCondition) {
        return axios.get('<%= ctxPath %>/api/emp/chart-pie', {
            params: {
                condition: searchCondition
            }
        }).then(response => {
            // 서버에서 받은 데이터 반환
            console.log(response);
            return response.data.success.responseData;
        }).catch(error => {
            console.error(error);
            return [];
        });
    }

    function resetChartContainer() {
        $('div#chart_container').empty();
        $('div#table_container').empty();
        $('div#highcharts-data-table').empty();
    }

    function createBasicLineChart(response) {
        resetChartContainer();

        console.log(response.data);
        // response.data에서 연도별로 성별 데이터 분리
        const years = [...new Set(response.data.map(item => item.year))].sort((a, b) => a - b);
        const maleData = years.map(year => {
            const found = response.data.find(item => item.year === year && item.name === "남자");
            return found ? found.y : null;
        });
        const femaleData = years.map(year => {
            const found = response.data.find(item => item.year === year && item.name === "여자");
            return found ? found.y : null;
        });

        Highcharts.chart('chart_container', {

            title: {
                text: response.label
            },

            subtitle: {
                text: `Source: <a href="https://irecusa.org/programs/solar-jobs-census/" target="_blank">\${response.thead}</a>`
            },

            yAxis: {
                title: {
                    text: response.thead
                }
            },

            xAxis: {
                categories: years,
                title: { text: '년도' }
            },

            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'middle'
            },



            series: [
                { name: '남자', data: maleData },
                { name: '여자', data: femaleData }
            ],

            responsive: {
                rules: [{
                    condition: {
                        maxWidth: 500
                    },
                    chartOptions: {
                        legend: {
                            layout: 'horizontal',
                            align: 'center',
                            verticalAlign: 'bottom'
                        }
                    }
                }]
            }

        });
    }


    function createPieChart(response) {
        resetChartContainer();
        Highcharts.chart('chart_container', {
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false,
                type: 'pie'
            },
            title: {
                text: response.label
            },
            tooltip: {// 소수부 두자리까지
                pointFormat: '{series.name}: <b>{point.percentage:.2f}%</b>'
            },
            accessibility: {
                point: {
                    valueSuffix: '%'
                }
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        format: '<b>{point.name}</b>: {point.percentage:.2f} %'//소수부 두자리까지 .2f
                    }
                }
            },
            series: [{
                name: '인원 통계',
                colorByPoint: true,
                data: response.data.map(item => ({
                    name: item.name,
                    y: item.y,
                    sliced: item.sliced,
                    selected: item.selected
                }))
            }]
        });
        //테이블 생성
        let html = `<table>
                        <tr>
                            <th>\${response.thead}</th>
                            <th>인원수</th>
                            <th>비율</th>
                        <tr/>`
        $.each(response.data, (i, item) => {
            console.log(item);
            html += `<tr>
                            <td>\${item.name}</td>
                            <td>\${item.count}명</td>
                            <td>\${item.y.toFixed(2)}%</td>
                        </tr>`;
        })

        html += `</table>`;

        $('#table_container').append(html);
    }


</script>

<jsp:include page="../../footer/footer2.jsp"/>
