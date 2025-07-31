package com.github.jpaquerydslmybatis.web.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomResponse<T> {
    private SuccessDetail<T> success;


    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class SuccessDetail<T> {
        private  int code;
        private  HttpStatus httpStatus;
        private  String message;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private  T responseData;
        private  LocalDateTime timestamp;



        public static <T> SuccessDetail<T> of(HttpStatus httpStatus, String message, T data) {
            return new SuccessDetail<>(httpStatus.value(), httpStatus, message, data, LocalDateTime.now());
        }
    }
    public HttpStatus getHttpStatus(){
        return this.success.getHttpStatus();
    }

    public static <T> CustomResponse<T> of(HttpStatus httpStatus, String message, T data){
        return new CustomResponse<>(
                SuccessDetail.of(httpStatus, message, data)
        );
    }
    public static <T> CustomResponse<T> ofOk(String message, T data){
        return new CustomResponse<>(
                SuccessDetail.of(HttpStatus.OK, message, data)
        );
    }
    public static <T> CustomResponse<T> emptyData(HttpStatus httpStatus, String message){
        return new CustomResponse<>(
                SuccessDetail.of(httpStatus, message, null)
        );
    }
    public static <T> CustomResponse<T> emptyDataOk(String message){
        return new CustomResponse<>(
                SuccessDetail.of(HttpStatus.OK, message, null)
        );
    }

}
