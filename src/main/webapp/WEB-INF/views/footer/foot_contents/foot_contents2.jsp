<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%-- ======= footer 페이지 만들기  ======= --%>

<div style="color: #999999; margin: auto; padding: 7px;">
쌍용교육센터 | 강남 서울특별시 강남구 테혜란로 132(역삼동) 한독약품빌딩 8층 쌍용교육센터 | 강남 Tel 02)3482-4632-5 Fax 02)3482-4636<br>
강북 서울특별시 마포구 월드컵북로 21 풍성빌딩 2,3,4층 쌍용강북교육센터 | 강북  Tel 02)336-8546-8 Fax 02)334-5405<br>
사업자번호 : 214-85-29296 | 대표 : 노경한 | 개인정보처리관리책임자 : 장일규<br>       
Copyright ⓒ 2012 Ssangyong institute of System Technology. All Right Reserved
</div>

<script type="text/javascript">

    $(function(){

        var mycontent_height = $("div#mycontent").css("height");
        var mysideinfo_height = $("div#mysideinfo").css("height");

        //   console.log("mycontent_height : " + mycontent_height);
        //   console.log("mysideinfo_height : " + mysideinfo_height);

        if (mycontent_height > mysideinfo_height) {
            $("div#mysideinfo").css({"height":mycontent_height});
        }

        if (mysideinfo_height > mycontent_height) {
            $("div#mycontent").css({"height":mysideinfo_height});
        }

    });

</script>