package com.github.jpaquerydslmybatis.service.email;

import com.github.jpaquerydslmybatis.common.exception.CustomBindException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@RequiredArgsConstructor
@Service
public class EmailSenderService {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String senderEmail; // 메일 발송자 이메일 주소


    private MimeMessageHelper createMailSenderHelper(MimeMessage mimeMessage,List<String> toList, String subject, String content) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);//true는 멀티파트모드
        helper.setTo(toList.toArray(new String[0])); // 받는 사람 이메일 주소
        helper.setSubject(subject);
        helper.setText(content, true);
        return helper;
    }
    private void setAttachments(MimeMessageHelper helper, MultipartFile file) throws MessagingException {
        if (file == null || file.isEmpty())
            return;
        String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "attachment";
        helper.addAttachment(fileName, file);
    }

    private void setAttachments(MimeMessageHelper helper, List<MultipartFile> fileList) throws MessagingException {
        if (fileList == null || fileList.isEmpty())
            return;
        for( MultipartFile file : fileList) {
            setAttachments(helper, file);
        }
    }

    public boolean sendIncludingAttachment(List<String> toList, String subject, String content, List<MultipartFile> files) {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            // MimeMessageHelper를 사용하여 메일 설정
            MimeMessageHelper helper = createMailSenderHelper(mimeMessage, toList, subject, content);
            // 첨부파일 설정
            setAttachments(helper, files);
            //메일 발송
            mailSender.send(mimeMessage);
            return true; // 메일 전송 성공

        } catch (MessagingException e) {
            e.printStackTrace();
            return false; // 메일 설정 오류
        }
    }
}
