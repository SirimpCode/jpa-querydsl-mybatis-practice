package com.github.jpaquerydslmybatis.common.converter.mapper.comment;

import com.github.jpaquerydslmybatis.repository.db1.jpa.comment.MyComment;
import com.github.jpaquerydslmybatis.web.dto.comment.CommentChildren;
import com.github.jpaquerydslmybatis.web.dto.comment.CommentParent;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    CommentParent commentToCommentDetail(MyComment myComment);
    CommentChildren map(MyComment value);
    List<CommentChildren> map(List<MyComment> value);
}
