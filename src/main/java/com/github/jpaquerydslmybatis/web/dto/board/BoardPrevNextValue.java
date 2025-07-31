package com.github.jpaquerydslmybatis.web.dto.board;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoardPrevNextValue {
    private Long prevBoardId;
    @JsonIgnore
    private String prevTitle;
    private Long nextBoardId;
    @JsonIgnore
    private String nextTitle;


    //생성 메서드
    public static BoardPrevNextValue fromPrevNext(BoardSimpleValue prev, BoardSimpleValue next){
        BoardPrevNextValue value = new BoardPrevNextValue();
        if(prev != null){
            value.prevBoardId = prev.getBoardId();
            value.prevTitle = prev.getTitle();
        }
        if(next != null){
            value.nextBoardId = next.getBoardId();
            value.nextTitle = next.getTitle();
        }
        return value;
    }
    public String getRefinedPrevTitle() {
        if (this.prevTitle == null)
            return null;
        //30 자 이상이면 28자로 줄이고 .. 붙이기
        return prevTitle.length() >= 30 ?
                prevTitle.substring(0, 28) + "..." : prevTitle;
    }
    public String getRefinedNextTitle() {
        if (this.nextTitle == null)
            return null;
        //30 자 이상이면 28자로 줄이고 .. 붙이기
        return nextTitle.length() >= 30 ?
                nextTitle.substring(0, 28) + "..." : nextTitle;
    }

}
