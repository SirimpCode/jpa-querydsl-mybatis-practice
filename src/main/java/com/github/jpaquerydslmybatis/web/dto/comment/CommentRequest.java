package com.github.jpaquerydslmybatis.web.dto.comment;

import lombok.Getter;
import lombok.Setter;

@Getter
public class CommentRequest {
    @Setter
    private String userId;
    private String content;
    private Long boardId;
    private Long parentCommentId;

    private String fileName;
    private String filePath;
    private Long fileSize;
}
