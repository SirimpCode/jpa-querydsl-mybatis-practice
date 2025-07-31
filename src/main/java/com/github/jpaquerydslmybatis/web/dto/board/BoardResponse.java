package com.github.jpaquerydslmybatis.web.dto.board;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.jpaquerydslmybatis.web.dto.auth.UserInfoResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BoardResponse {
    private Long boardId;
    private String title;
    private String content;
    private String password;
    private long viewCount;
    private boolean status;
    private LocalDateTime createdAt;

    private Long parentBoardId;
    private Long rootBoardId;

    private String fileName;
    private String filePath;
    private Long fileSize;

    private UserInfoResponse userInfo;

    //json 직렬화시 non null 설정하기
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BoardPrevNextValue boardPrevNextValue;

    public String getRefinedTitle(){
        if(title==null)
            return title;

        return title.length() >= 30 ?
                title.substring(0, 28) + "..." :
                title;
    }

}
