package com.github.jpaquerydslmybatis.service.storage;

import com.github.jpaquerydslmybatis.common.exception.CustomBindException;
import com.github.jpaquerydslmybatis.common.exception.CustomViewException;
import com.github.jpaquerydslmybatis.config.web.WebConfig;
import com.github.jpaquerydslmybatis.web.dto.storage.FileResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;


@Service
@Slf4j
@RequiredArgsConstructor
public class StorageService {
    private final WebConfig webConfig;



    /**
     * 파일이 저장될 폴더 경로 생성 메서드 1번째 인자는 첫번째 폴더 이름, 2번째 인자는 두번째 폴더 이름
     * 1번째 폴더는 테이블명, 2번째 폴더는 해당 행의 프라이머리 키 값 추천.
     * 3번째 인자는 폴더가 3개까지 필요할시 사용
     * 예시) product/1, product/2, product/3, ... 등등
     */
    public Path createFileDirectory(String firstFolder, String secondFolder, String thirdFolder) {
        Path uploadDir = Paths.get(webConfig.getEndPath(), firstFolder, secondFolder, thirdFolder);
        createFolder(uploadDir);
        return uploadDir;
    }

    /**
     * 폴더경로가 두개만 필요할경우 파라미터를 두개만 넣고 보냄
     */
    public Path createFileDirectory(String firstFolder, String secondFolder) {
        Path uploadDir = Paths.get(webConfig.getEndPath(), firstFolder, secondFolder);
        createFolder(uploadDir);
        return uploadDir;
    }

    /**
     * 폴더 생성 메서드 실패시 log에 기록을 남기고 익셉션을 던짐
     */
    private void createFolder(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            log.error("폴더 생성 실패: {}", e.getMessage(), e);
            throw CustomBindException.of()
                    .customMessage("폴더 생성에 실패했습니다. 관리자에게 문의해주세요.")
                    .build();
        }
    }

    /**
     * 원본 파일명을 반환하는 메서드 필요시 사용 파일이 없는상태에서 호출시 null 이 반환 되니 주의 !!
     */
    public String getOriginalFileName(MultipartFile file) {
        return file.getOriginalFilename();
    }

    /**
     * uploadPath에 파일을 저장 후 Web 에서 사용되는 경로를 반환
     * 파일이 없을 시 null 반환 주의 !!!
     * prefix 는 파일 이름 앞에 붙는 문자열로 예를들어 productImage1_ 등등
     */
    public FileResponse returnTheFilePathAfterTransfer(MultipartFile file, Path uploadDir, String prefix) {
        try {
            checkFile(file);// 파일이 비어있으면 null 반환
        } catch (NullPointerException e) {
            return null;
        }
        String originalFileName = Objects.requireNonNull(file.getOriginalFilename());
        Path destination = uploadDir.resolve(prefix + originalFileName);
        saveFile(file, destination);


        return createFileResponse(destination, file);
    }

    /**
     * 응답 생성 메서드
     */
    private FileResponse createFileResponse(Path destination, MultipartFile file) {
        String originalFileName = Objects.requireNonNull(file.getOriginalFilename());
        String filePath = createFilePath(destination);
        String fileUrl = createWebPath(filePath);
        Long fileSize = file.getSize();
        String fileType = file.getContentType();

        return FileResponse.of(originalFileName, filePath, fileUrl, fileSize, fileType);

    }

    /**
     * 파일 앞에 붙일 단어가 없을때. 그냥 원본파일명으로 저장된다.
     */
    public FileResponse returnTheFilePathAfterTransfer(MultipartFile file, Path uploadDir) {
        return this.returnTheFilePathAfterTransfer(file, uploadDir, "");
    }

    private void saveFile(MultipartFile file, Path destination) {
        try {
            file.transferTo(destination.toFile());
        } catch (IOException e) {
            log.error("파일 저장 중 오류 발생: {}", e.getMessage(), e);
            throw CustomBindException.of()
                    .customMessage("파일 저장 중 오류가 발생했습니다. 다시 시도해주세요.")
                    .build();
        }
    }


    /**
     * byte[] 파일을 저장하는 메소드 byte[] 형식으로 전달받으면
     * 원본파일명과 확장자를 알 수가 없다. 따라서 요청 파라미터에서 받아와야함.
     */
    public String returnTheFilePathAfterTransfer(byte[] file, Path uploadDir, String fileName) {

//        String originalFileName = Objects.requireNonNull(file.());
        String originalFileName = fileName + ".jpg"; // 확장자는 jpg로 가정
        Path destination = uploadDir.resolve(originalFileName);
        saveFile(file, destination);

        String filePath = createFilePath(destination);

        return createWebPath(filePath);
    }

    /**
     * byte[] 파일을 저장하는 메소드
     */
    private void saveFile(byte[] file, Path destination) {
        try {
            Files.write(destination, file);
            log.info("파일이 성공적으로 저장되었습니다: {}", destination);
        } catch (IOException e) {
            log.error("파일 저장 중 오류 발생: {}", e.getMessage(), e);
            throw CustomBindException.of().customMessage("파일 저장 중 오류가 발생했습니다. 다시 시도해주세요.").build();
        }
    }


    private void checkFile(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new NullPointerException("파일이 비어있음");
        if (file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
            log.error("알 수 없는 오류 : 파일 이름이 비어있습니다. 확인값 : {} ", file.getOriginalFilename());
            throw CustomBindException.of()
                    .customMessage("알 수 없는 오류 : 파일 이름이 비어있습니다.")
                    .build();
        }
    }

    /**
     * 파일 경로를 생성하는 메소드
     */
    private String createFilePath(Path destination) {
        // 파일 경로를 '/'로 변경하기
        if (destination == null)
            return null;
        return normalizePath(destination.toString());
    }

    /**
     * 파일 경로를 웹에서 접근할 수 있는 경로로 변환하고 앞부분에 서버 url 을 추가해주는 메서드
     */
    private String createWebPath(String filePath) {
        // 파일 경로를 '/'로 변경하고, uploadPath를 '/uploads/'로 대체
        if (filePath == null)
            return null;
        return filePath.replaceFirst("^" + Pattern.quote(webConfig.getEndPath()), webConfig.getUploadPath());
    }

    /**
     * 경로를  '\'  에서 '/'로 변경하는 메소드
     */
    private String normalizePath(String path) {
        return path.replace('\\', '/');
    }


    /** 삭제나 다운로드등 파일의 실제경로를 알아오기위한 메서드*/
    private String getRealFullFilePath(String filePath){
        String endPathWithoutUploads = webConfig.getEndPath().replace(webConfig.getUploadPath(), "");
        return endPathWithoutUploads + filePath;
    }

    /**파일 다운로드 로직 filePath 값으로 불러와 원본파일명으로 저장한다.*/
    public void getDownloadUrl(String filePath, String fileName, HttpServletResponse response) {
        String realFullPath = getRealFullFilePath(filePath);
        File file = new File(realFullPath);
        if (!file.exists())
            throw new CustomViewException("파일이 존재하지 않습니다.", null);

        response.setContentType("application/octet-stream");
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        // 한글 파일명 대응: filename, filename* 둘 다 설정
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName);
        // 파일 크기 설정
        response.setHeader("Content-Length", String.valueOf(file.length()));

        try (
                InputStream in = new BufferedInputStream(new FileInputStream(file));
                OutputStream out = response.getOutputStream();
        ) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException("파일 다운로드 중 오류 발생", e);
        }
    }

    public void deleteFile(List<String> filePath) {
        List<String> realFullPaths = filePath.stream()
                .map(this::getRealFullFilePath)
                .toList();

        List<Path> filesToDelete = realFullPaths.stream()
                .map(Paths::get)
                .toList();

        try {
            for (Path fileToDelete : filesToDelete) {
                boolean isDelete = Files.deleteIfExists(fileToDelete);
                if (!isDelete) {
                    log.warn("파일이 존재하지 않거나 삭제할 수 없습니다: {}", fileToDelete);
                    continue; // 파일이 존재하지 않으면 다음 파일로 넘어감
                }
                log.info("파일이 성공적으로 삭제되었습니다: {}", fileToDelete);
            }

        } catch (IOException e) {
            log.error("파일 삭제 중 오류 발생: {}", e.getMessage(), e);
            throw CustomBindException.of()
                    .customMessage("파일 삭제 중 오류가 발생했습니다. 다시 시도해주세요.")
                    .build();
        }
    }
}
