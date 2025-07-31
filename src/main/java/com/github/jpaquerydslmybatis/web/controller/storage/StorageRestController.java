package com.github.jpaquerydslmybatis.web.controller.storage;

import com.github.jpaquerydslmybatis.service.storage.StorageService;
import com.github.jpaquerydslmybatis.web.dto.response.CustomResponse;
import com.github.jpaquerydslmybatis.web.dto.storage.FileResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/storage")
public class StorageRestController {
    private final StorageService storageService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomResponse<List<FileResponse>> fileListUploadTester(
            @RequestPart List<MultipartFile> fileList,
            @RequestParam String firstFolder,
            @RequestParam String secondFolder,
            @RequestParam(required = false) String thirdFolder,
            @RequestParam(required = false) String prefix
    ) {

        Path uploadDir = thirdFolder != null ?
                storageService.createFileDirectory(firstFolder, secondFolder, thirdFolder) :
                storageService.createFileDirectory(firstFolder, secondFolder);

        List<FileResponse> fileResponses = new ArrayList<>();
        for (MultipartFile file : fileList) {
            if (!file.isEmpty()) {
                FileResponse response =
                        prefix != null ?
                                storageService.returnTheFilePathAfterTransfer(file, uploadDir, prefix) :
                                storageService.returnTheFilePathAfterTransfer(file, uploadDir);
                fileResponses.add(response);
            }
        }
        return CustomResponse.ofOk("파일 업로드 성공", fileResponses);
    }
    //첨부파일 다운로드
    @GetMapping("/download")
    public CustomResponse<Void> downloadFileRequiredLogin(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam String filePath,
            @RequestParam String fileName
    ) {
        storageService.getDownloadUrl(filePath, fileName, response);
        return CustomResponse.emptyDataOk("파일 다운로드 URL 생성 성공");
    }
    //첨부파일 삭제
    @DeleteMapping
    public CustomResponse<Void> deleteFile(
            @RequestParam List<String> filePath
    ) {
        storageService.deleteFile(filePath);
        return CustomResponse.emptyDataOk("파일 삭제 성공");
    }


}
