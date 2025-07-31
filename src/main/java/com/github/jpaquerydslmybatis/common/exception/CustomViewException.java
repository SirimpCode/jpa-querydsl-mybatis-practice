package com.github.jpaquerydslmybatis.common.exception;

import lombok.Getter;
import org.springframework.web.bind.annotation.ExceptionHandler;
@Getter
public class CustomViewException extends RuntimeException {
  private final String loc;

    public CustomViewException(String message, String loc) {
        super(message);
        this.loc = loc;
    }
}
