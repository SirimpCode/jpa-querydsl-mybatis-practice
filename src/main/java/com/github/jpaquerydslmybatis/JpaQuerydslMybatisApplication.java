package com.github.jpaquerydslmybatis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
// Application 클래스에 @EnableAspectJAutoProxy 를 추가하여 AOP(Aspect Oriented Programming)클래스를 찾을 수 있게 해준다. 우리는 com.spring.app.aop.CommonAop 이 AOP 클래스 이다.
@SpringBootApplication
public class JpaQuerydslMybatisApplication {

    public static void main(String[] args) {
        SpringApplication.run(JpaQuerydslMybatisApplication.class, args);
    }

}
