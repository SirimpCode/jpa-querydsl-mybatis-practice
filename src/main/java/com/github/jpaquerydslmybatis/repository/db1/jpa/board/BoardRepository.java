package com.github.jpaquerydslmybatis.repository.db1.jpa.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {
    @Query("SELECT b FROM Board b JOIN FETCH b.myUser")
    List<Board> findAllFetchJoinUser();

    @Query("SELECT b FROM Board b JOIN FETCH b.myUser WHERE b.boardId = :boardId")
    Optional<Board> findByIdFetchJoinUser(Long boardId);
    @Query("SELECT b.password FROM Board b WHERE b.boardId = :boardId")
    String findPasswordByBoardId(Long boardId);

    @Query("SELECT b.myUser.userId FROM Board b WHERE b.boardId = :boardId")
    String findWriterIdByBoardId(Long boardId);
}
