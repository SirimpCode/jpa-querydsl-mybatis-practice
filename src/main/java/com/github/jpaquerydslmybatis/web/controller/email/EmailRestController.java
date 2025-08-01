package com.github.jpaquerydslmybatis.web.controller.email;

import com.github.jpaquerydslmybatis.service.email.EmailSenderService;
import com.github.jpaquerydslmybatis.web.dto.response.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/email")
public class EmailRestController {
    private final EmailSenderService emailSenderService;


    @PostMapping(value = "/send",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomResponse<?> sendEmail(@RequestPart("file_arr") List<MultipartFile> file,
                                       @RequestParam("recipientArr") List<String> toList,
                                       @RequestParam("subject") String subject,
                                        @RequestParam("content") String content){
        // 이메일 전송 로직을 여기에 구현
        boolean result = emailSenderService.sendIncludingAttachment(toList, subject, content, file);
        return CustomResponse.emptyDataOk("이메일이 성공적으로 전송되었습니다.");
    }
}
