package com.github.jpaquerydslmybatis.web.dto.music.sub;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class Artist {
    private final String artistId;
    private final String name;

}
