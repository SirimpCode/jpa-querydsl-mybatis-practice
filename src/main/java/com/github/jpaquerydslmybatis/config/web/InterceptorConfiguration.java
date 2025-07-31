package com.github.jpaquerydslmybatis.config.web;

import com.github.jpaquerydslmybatis.web.intercepter.AdminCheckInterceptor;
import com.github.jpaquerydslmybatis.web.intercepter.LoginCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class InterceptorConfiguration implements WebMvcConfigurer {

    private final LoginCheckInterceptor loginCheckInterceptor;
    private final AdminCheckInterceptor adminCheckInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns("/interceptor/member_only/**");
        registry.addInterceptor(adminCheckInterceptor)
                .addPathPatterns("/interceptor/special_member/**");


        WebMvcConfigurer.super.addInterceptors(registry);
    }

    /*
        addInterceptor() : 인터셉터를 등록해준다.
        addPathPatterns() : 인터셉터를 호출하는 주소와 경로를 추가한다.
        excludePathPatterns() : 인터셉터 호출에서 제외하는 주소와 경로를 추가한다.
     */
}
