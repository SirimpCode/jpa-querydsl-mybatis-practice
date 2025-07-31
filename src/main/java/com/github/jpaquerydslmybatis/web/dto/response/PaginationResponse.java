package com.github.jpaquerydslmybatis.web.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class PaginationResponse<T> {

    private long totalElements;
    private long totalPages;
    private boolean hasNext;
    private List<T> Elements;
    public static <T> PaginationResponse<T> of(long totalElements, List<T> elements, long currentPage, long currentSize) {
        PaginationResponse<T> response = new PaginationResponse<>();
        response.totalElements = totalElements;
        response.Elements = elements;
        if (currentSize == 0)
            return response;
        response.totalPages = (totalElements + currentSize - 1) / currentSize; // Calculate total pages
        // 현재 페이지는 0부터 시작하므로 currentPage가 0일 때는 첫 페이지로 간주
        response.hasNext = currentPage < response.totalPages - 1; // Check if there is a next page
        return response;
    }

}
