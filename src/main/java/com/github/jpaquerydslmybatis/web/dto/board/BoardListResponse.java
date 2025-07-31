package com.github.jpaquerydslmybatis.web.dto.board;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class BoardListResponse {
    private Long boardId;
    private String title;
    private String writer;
    private LocalDateTime createdAt;
    private long viewCount;
    private String content;
    private long commentCount;
    private Long parentBoardId;
    private boolean isFileAttached;

    private List<BoardListResponse> childrenBoards;

    public String getRefinedTitle() {
        if (this.title == null)
            return null;
        //30 자 이상이면 28자로 줄이고 .. 붙이기
        return title.length() >= 30 ?
                title.substring(0, 28) + "..." : title;
    }


    public void addChildren(BoardListResponse childBoard) {
        if (this.childrenBoards == null) {
            this.childrenBoards = new ArrayList<>();
        }
        this.childrenBoards.add(childBoard);
    }
}
