package com.github.jpaquerydslmybatis.repository.db1.mybatis.mapper.board;

import com.github.jpaquerydslmybatis.web.domain.db1.board.BoardSimpleDomain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BoardMybatisMapper {

    //Param에다가 boardId를 넣어주면 xml에서 ${boardId}로 또는 #{boardId}로 사용할 수 있다.
    // 이때 ${}는 문자열로 치환되고, #{ }는 PreparedStatement의 파라미터로 치환된다.

    BoardSimpleDomain findById(@Param("boardId") Long boardId);
}
