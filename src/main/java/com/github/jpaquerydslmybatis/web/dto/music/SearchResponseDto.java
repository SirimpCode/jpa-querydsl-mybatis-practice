package com.github.jpaquerydslmybatis.web.dto.music;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class SearchResponseDto {
    private String artistName;
    private String title;
    private String albumName;
    private String imageUrl;
}
