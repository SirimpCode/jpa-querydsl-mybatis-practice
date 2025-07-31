package com.github.jpaquerydslmybatis.repository.db1.jpa.board;

import com.github.jpaquerydslmybatis.web.dto.board.BoardListResponse;
import com.github.jpaquerydslmybatis.web.dto.board.BoardPrevNextValue;
import com.github.jpaquerydslmybatis.web.dto.board.BoardSearchRequest;
import com.github.jpaquerydslmybatis.web.dto.response.PaginationResponse;

public interface BoardRepositoryCustom {
    BoardPrevNextValue findPrevNextById(Board board, BoardSearchRequest searchRequest);

    PaginationResponse<BoardListResponse> findBoardListByCondition(BoardSearchRequest searchRequest, long page, long size);
}
