package com.github.jpaquerydslmybatis.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class EmailVerifyService {
//    public static HttpServletRequest getRequest() {
//        ServletRequestAttributes attrs =
//                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        return attrs.getRequest();
//    }RequestContextHolder 를 활용하여 세션을 불러오기
//
//    public static HttpSession getSession() {
//        return getRequest().getSession();
//    }


    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String senderEmail; // 메일 발송자 이메일 주소

    //메일 발송
    public boolean sendVerifyCodeToEmail(String to, HttpSession session){
        String verifyCode = generateRandomCode();
        try {
            //html 파일 읽기
            String htmlContent = Files.readString(Paths.get("src/main/resources/static/email/email_verify.html"));
            // 플레이스홀더 {{verifyCode}}를 실제 인증 코드로 치환
            htmlContent = htmlContent.replace("{{verifyCode}}", verifyCode);
            htmlContent = htmlContent.replace("{{email}}", senderEmail);


            MimeMessage mimeMessage = mailSender.createMimeMessage();


            //수신자, 제목 , 본문
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setTo(to);
            helper.setSubject("BagEnd Email 인증코드");
            helper.setText(htmlContent, true);
            //메일 보내기
            mailSender.send(mimeMessage);
            //레디스 저장 (유효 기간 10분)
//            redisRepository.save(to, verifyCode, Duration.ofMinutes(10)); 레디스말고 세션저장

            //세션에 저장 유효기간 10분 같이저장
            saveSessionCode(session, to, verifyCode);
            System.out.println("인증 코드가 이메일로 전송되었습니다: " + verifyCode);
            return true;

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read HTML template", e);
        }catch (Exception e){
            throw new RuntimeException("메일 발송에 실패했습니다.", e);
        }

    }
    public void saveSessionCode(HttpSession session, String to, String verifyCode){
        long expireAt = System.currentTimeMillis() + 10 * 60 * 1000;
        session.setAttribute(to + "_code", verifyCode);
        session.setAttribute(to + "_expireAt", expireAt);
    }

    //영문자 랜덤 5자리 + 숫자 7자리 조합
    //지금은 숫자 6자리로 변경
    public String generateRandomCode() {
        StringBuilder code = new StringBuilder();
        Random random = new Random();
//        //영문자 5자리
//        for (int i = 0; i < 5; i++) {
//            char ch = (char) ('A' + random.nextInt(26)); // A-Z
//            code.append(ch);
//        }
        //숫자 6자리
        for (int i = 0; i < 6; i++) {
            int digit = random.nextInt(10); // 0-9
            code.append(digit);
        }
        return code.toString();
    }

    //랜덤 코드 생성
    private int generateRandomNumber() {
        Random random = new Random();
        return 100000 + random.nextInt(900000);
    }


    public boolean verifyEmail(String email, String code, HttpSession session) {
        //세션에서 인증 코드 가져오기
        String sessionCode = (String) session.getAttribute(email+ "_code");
        Long sessionExpireAt = (Long) session.getAttribute(email + "_expireAt");

        if (sessionExpireAt == null || System.currentTimeMillis() > sessionExpireAt) {
            // 인증 코드가 만료되었으면 세션에서 제거
            session.removeAttribute(email + "_code");
            session.removeAttribute(email + "_expireAt");
            return false; // 인증 실패
        }
        //세션에 인증 코드가 없거나, 입력한 코드와 세션의 코드가 일치하지 않으면 false 반환
        if (sessionCode == null || !sessionCode.equals(code)) return false;
        //인증 코드가 일치하면 세션에서 인증 코드 제거

        session.removeAttribute(email + "_code");
        session.removeAttribute(email + "_expireAt");
        return true; //인증 성공
    }
}
