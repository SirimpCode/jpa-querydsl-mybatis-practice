package com.github.jpaquerydslmybatis.repository.db1.mybatis.board;

import com.github.jpaquerydslmybatis.repository.db1.mybatis.mapper.board.BoardMybatisMapper;
import com.github.jpaquerydslmybatis.web.domain.db1.board.BoardSimpleDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BoardDao {
    private final BoardMybatisMapper boardMybatisMapper;



    public BoardSimpleDomain findById(Long boardId) {
        return boardMybatisMapper.findById(boardId);
    }
}
