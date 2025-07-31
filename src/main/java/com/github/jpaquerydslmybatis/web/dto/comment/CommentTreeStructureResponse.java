package com.github.jpaquerydslmybatis.web.dto.comment;

import com.github.jpaquerydslmybatis.common.myenum.Status;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CommentTreeStructureResponse {
    private Long commentId;
    private String content;
    private Status status;
    private LocalDateTime createdAt;
    private String userId;
    private String username;
    private Long parentCommentId;
    private List<CommentTreeStructureResponse> children;

    public void addChild(CommentTreeStructureResponse child) {
        if (children == null) children = new ArrayList<>();
        children.add(child);
    }
}
