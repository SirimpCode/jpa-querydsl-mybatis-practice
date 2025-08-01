//package com.github.jpaquerydslmybatis.config.server;
//
//import jakarta.servlet.MultipartConfigElement;
//import org.apache.catalina.connector.Connector;
//import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
//import org.springframework.boot.web.server.WebServerFactoryCustomizer;
//import org.springframework.boot.web.servlet.MultipartConfigFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.util.unit.DataSize;
//
//@Configuration
//public class TomcatConfig {
//
//    @Bean
//    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
//        return factory -> factory.addConnectorCustomizers(connector -> {
//            connector.setProperty("maxPartCount", "-1");//갯수 조절 필요
//        });
//    }
//
////    @Bean
////    public MultipartConfigElement multipartConfigElement() {
////        MultipartConfigFactory factory = new MultipartConfigFactory();
////        factory.setMaxFileSize(DataSize.ofMegabytes(100)); // 파일 크기 제한
////        factory.setMaxRequestSize(DataSize.ofMegabytes(200));// 요청 크기 제한
////        return factory.createMultipartConfig();
////    }
//}