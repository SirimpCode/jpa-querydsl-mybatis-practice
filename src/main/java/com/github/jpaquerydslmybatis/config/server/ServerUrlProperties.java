package com.github.jpaquerydslmybatis.config.server;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.server-url")
public class ServerUrlProperties {//스웨거에서쓰일거임
    private String base;
    private String https;
}
