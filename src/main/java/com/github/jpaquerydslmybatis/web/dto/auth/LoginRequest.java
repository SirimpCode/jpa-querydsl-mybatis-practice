package com.github.jpaquerydslmybatis.web.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class LoginRequest {
    @JsonProperty(value = "userid", access =  JsonProperty.Access.WRITE_ONLY)
    private String userId;
    @JsonProperty(value = "pwd", access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    public void setUserid(String userid){
        this.userId = userid;
    }
    public void setPwd(String password){
        this.password = password;
    }
}
