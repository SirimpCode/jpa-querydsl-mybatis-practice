package com.github.jpaquerydslmybatis.web.dto.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class FileResponse {
    private final String fileName;
    private final String filePath;
    private final String fileUrl;
    private final Long fileSize;
    private final String fileType;
}
