package com.github.jpaquerydslmybatis.repository.db1.jpa.board;

import com.github.jpaquerydslmybatis.repository.db1.jpa.user.MyUser;
import com.github.jpaquerydslmybatis.web.dto.board.BoardWriteRequest;
import com.github.jpaquerydslmybatis.web.dto.storage.FileInfoUpdateRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@DynamicInsert
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Setter
    private String title;
    @Setter
    private String content;
    @Setter
    private String password;
    private long viewCount;

    private Boolean status;
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_board_id")
    private Board parentBoard;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "root_board_id")
    private Board rootBoard;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "rootBoard")
    private List<Board> allChildrenBoards;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentBoard")
    private List<Board> childrenBoards;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @Setter
    private MyUser myUser;

    private String fileName;
    private String filePath;
    private Long fileSize;


    public static Board onlyId(Long boardId) {
        Board board = new Board();
        board.boardId = boardId;
        return board;
    }
    public void setParentAndRootById(Long parentId, Long rootId){
        this.parentBoard = Board.onlyId(parentId);
        this.rootBoard = Board.onlyId(rootId);
    }

    //조회수 증가 메서드
    public void incrementViewCount() {
        this.viewCount++;
    }


    public void modifyBoard(BoardWriteRequest writeRequest) {
        this.title = writeRequest.getTitle();
        this.content = writeRequest.getContent();
        this.password = writeRequest.getPassword();
    }

    public void fileInfoUpdate(FileInfoUpdateRequest request) {
        this.fileName = request.getFileName();
        this.filePath = request.getFilePath();
        this.fileSize = request.getFileSize();
    }
}
