package com.github.jpaquerydslmybatis.web.dto.comment;

import com.github.jpaquerydslmybatis.common.myenum.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CommentParent {
    private Long commentId;
    private String content;
    private Status status;
    private LocalDateTime createdAt;
    private List<CommentChildren> childrenComments;
    private String userId;
    private String username;

    private String fileName;
    private String filePath;
    private Long fileSize;
}
