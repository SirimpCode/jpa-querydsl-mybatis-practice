package com.github.jpaquerydslmybatis.config.web;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${file.upload.directory}")
    @Getter
    private String uploadPath;
    private final String BASE_PATH = System.getProperty("user.dir"); // 현재 작업 디렉토리 경로
    @Getter
    private String endPath;

    @PostConstruct
    private void init() {
        endPath = normalizePath(BASE_PATH) + uploadPath;
    }


    @Override
    public void addResourceHandlers(@NotNull ResourceHandlerRegistry registry) {

        // 외부 업로드 폴더 매핑
        registry.addResourceHandler("/uploads/**")//예 /uploads/image.jpg 즉 /uploads/로 시작하는 URL 요청을 처리
                .addResourceLocations(
                        (endPath.startsWith("/") ? "file:" : "file:/")
                                + this.endPath
                ) // file:///은 시스템의 루트 경로를 뜻함 그이후 C:/upload/ 등등을 작성
                .setCachePeriod(3600);

        // 기존 static 리소스 매핑
        registry.addResourceHandler("/**")// 그외에 모든 URL 요청을 처리
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);
    }

    private String normalizePath(String path) {
        return path.replace('\\', '/');
    }

}
