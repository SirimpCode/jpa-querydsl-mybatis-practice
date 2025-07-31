package com.github.jpaquerydslmybatis.web.dto.board;

import lombok.Getter;
import lombok.Setter;

@Getter
public class BoardWriteRequest {
    private String title;
    private String content;
    private String password;
    private Long boardId;
    private Long parentBoardId;
    private Long rootBoardId;

    private String fileName;
    private String filePath;
    private Long fileSize;
}
