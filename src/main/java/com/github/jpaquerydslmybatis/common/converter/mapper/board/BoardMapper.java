package com.github.jpaquerydslmybatis.common.converter.mapper.board;

import com.github.jpaquerydslmybatis.common.converter.mapper.user.MyUserMapper;
import com.github.jpaquerydslmybatis.repository.db1.jpa.board.Board;
import com.github.jpaquerydslmybatis.web.dto.board.BoardResponse;
import com.github.jpaquerydslmybatis.web.dto.board.BoardWriteRequest;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

//이걸 작성함으로서 MyUser Mapper를 사용해서 BoardResponse에 있는 userInfo를 MyUser로 변환할 수 있다.
@Mapper(uses = MyUserMapper.class)
public interface BoardMapper {
    BoardMapper INSTANCE = Mappers.getMapper(BoardMapper.class);


    Board writeRequestToBoard(BoardWriteRequest request);

    @AfterMapping// Board 엔티티로 변환된후에 사용되는 메서드 에프터매핑!
    default void setParentAndRoot(@MappingTarget Board board, BoardWriteRequest request) {
        if (request.getParentBoardId() != null && request.getRootBoardId() != null)
            board.setParentAndRootById(request.getParentBoardId(), request.getRootBoardId());
    }


    List<BoardResponse> boardsToResponses(List<Board> boards);

    @Mapping(source = "myUser", target = "userInfo")
    @Mapping(source = "parentBoard.boardId", target = "parentBoardId")
    @Mapping(source = "rootBoard.boardId", target = "rootBoardId")
    BoardResponse boardToResponse(Board board);
}
