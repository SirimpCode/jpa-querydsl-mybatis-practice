package com.github.jpaquerydslmybatis.web.dto.storage;

import lombok.Getter;

@Getter
public class FileInfoUpdateRequest {
    private String fileName;
    private String filePath;
    private Long fileSize;
    private Long primaryKey;

}
