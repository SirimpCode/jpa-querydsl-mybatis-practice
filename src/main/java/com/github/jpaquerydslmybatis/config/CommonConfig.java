package com.github.jpaquerydslmybatis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.function.Function;

@Configuration
public class CommonConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }



    @Bean//Sha-256 암호화
    public Function<String, String> sha256Encrypt() {
        return plainText -> {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] byteData = md.digest(plainText.getBytes());
                StringBuilder hexString = new StringBuilder();
                for (byte b : byteData) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                return hexString.toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
    private static final String SECRET_KEY = "abcd0070#seven$$";

    @Bean // AES256 암호화
    public Function<String, String> aes256Encrypt() {
        return plainText -> {
            try {
                String iv = SECRET_KEY.substring(0, 16);
                SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes("UTF-8"), "AES");
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, keySpec, new javax.crypto.spec.IvParameterSpec(iv.getBytes("UTF-8")));
                byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
                return Base64.getEncoder().encodeToString(encrypted);
            } catch (Exception e) {
                throw new RuntimeException("AES256 암호화 실패", e);
            }
        };
    }

    @Bean // AES256 복호화
    public Function<String, String> aes256Decrypt() {
        return encryptedText -> {
            try {
                String iv = SECRET_KEY.substring(0, 16);
                SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes("UTF-8"), "AES");
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, keySpec, new javax.crypto.spec.IvParameterSpec(iv.getBytes("UTF-8")));
                byte[] decoded = Base64.getDecoder().decode(encryptedText);
                byte[] decrypted = cipher.doFinal(decoded);
                return new String(decrypted, "UTF-8");
            } catch (Exception e) {
                throw new RuntimeException("AES256 복호화 실패", e);
            }
        };
    }
}
