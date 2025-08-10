package com.jtcwzy.mailservice.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;

@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "app")
public class PasswordConfig {
    
    private String password;
    
    private static String encryptedPassword;
    
    @PostConstruct
    public void init() {
        if (password != null && !password.isEmpty()) {
            encryptedPassword = encryptPassword(password);
            log.info("Password encrypted and stored");
        }
    }
    
    public static String getEncryptedPassword() {
        return encryptedPassword;
    }
    
    private String encryptPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}