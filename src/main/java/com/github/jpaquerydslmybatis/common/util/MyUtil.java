package com.github.jpaquerydslmybatis.common.util;

import jakarta.servlet.http.HttpServletRequest;

public class MyUtil {
    // *** ? 다음의 데이터까지 포함한 현재 URL 주소를 알려주는 메소드를 생성 *** //
    public static String getCurrentURL(HttpServletRequest request) {
        String currentUrl = request.getRequestURL().toString() +
                (request.getQueryString() != null ? "?" + request.getQueryString() : "");

        int first = currentUrl.indexOf('/');
        int second = currentUrl.indexOf('/', first + 1);
        int third = currentUrl.indexOf('/', second + 1);


        return  currentUrl.substring(third);

    }
}
