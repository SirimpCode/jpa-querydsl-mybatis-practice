package com.github.jpaquerydslmybatis.web.dto.board;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class BoardSearchRequest {
    private String searchValue;
    private  SearchType searchType;
    private SearchSort searchSort;

    public static BoardSearchRequest of(String searchValue, String searchType, String searchSort) {
        BoardSearchRequest request = new BoardSearchRequest();
        request.searchValue = searchValue;
        try{
            request.searchType = SearchType.valueOf(searchType.toUpperCase());
        }catch (IllegalArgumentException | NullPointerException e){
            request.searchType = SearchType.ALL;
        }
        try{
            request.searchSort = SearchSort.valueOf(searchSort.toUpperCase());
        }catch (IllegalArgumentException | NullPointerException e) {
            request.searchSort = SearchSort.CREATED_AT_DESC;
        }
        return request;
    }

    @AllArgsConstructor
    public enum  SearchType{
        ALL("전체"), TITLE("제목"), CONTENT("내용"), WRITER("작성자");
        private final String type;
    }
    @AllArgsConstructor
    public enum SearchSort {
        CREATED_AT_ASC("오래된순"), CREATED_AT_DESC("최신순"),  TITLE_ASC("제목오름차순"), TITLE_DESC("제목내림차순");
        private final String sort;

    }
}
