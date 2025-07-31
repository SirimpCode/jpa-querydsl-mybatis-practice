package com.github.jpaquerydslmybatis.repository.db1.jpa.comment;

import com.github.jpaquerydslmybatis.web.dto.comment.CommentParent;
import com.github.jpaquerydslmybatis.web.dto.comment.CommentTreeStructureResponse;
import com.github.jpaquerydslmybatis.web.dto.response.PaginationResponse;

import java.util.List;

public interface CommentRepositoryCustom {
    PaginationResponse<CommentParent> findCommentListByBoardId(Long boardId, long page, long size);

    List<CommentTreeStructureResponse> findCommentListTreeByBoardId(Long boardId);
}
