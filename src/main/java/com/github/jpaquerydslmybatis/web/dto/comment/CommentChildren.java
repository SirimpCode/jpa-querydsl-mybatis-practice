package com.github.jpaquerydslmybatis.web.dto.comment;

import com.github.jpaquerydslmybatis.common.myenum.Status;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CommentChildren {
    private Long commentId;
    private String content;
    private Status status;
    private LocalDateTime createdAt;
    private Long parentCommentId;
    private String userIdChild;
    private String usernameChild;

}
