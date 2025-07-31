package com.github.jpaquerydslmybatis.web.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class LoginResponse {
    private boolean passwordChange;
    private String goBackURL;
}
