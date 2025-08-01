package com.github.jpaquerydslmybatis.web.dto.music;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TrackInfoResponse {
    private final Long musicId;
    private final String albumId;
    private final List<Artist> artist;
    private final String title;
    private final String album;
    private final String genre;
    private final String releaseDate;
    private final long duration;
    private final String coverImageUrl;

    @Getter
    @AllArgsConstructor(staticName = "of")
    public static class Artist {
        private final Long artistId;
        private final String name;
        private final String artistImageUrl;
    }
    @Getter
    @AllArgsConstructor(staticName = "of")
    public static class Album {
        private final String albumId;
        private final String title;
        private final String coverImageUrl;
    }
}
