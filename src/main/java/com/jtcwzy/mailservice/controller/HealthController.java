package com.jtcwzy.mailservice.controller;

import com.jtcwzy.mailservice.dto.ApiResponse;
import com.jtcwzy.mailservice.service.MailProviderFactory;
import jakarta.mail.MessagingException;
import jakarta.mail.Store;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class HealthController {

    private final MailProviderFactory mailProviderFactory;

    @GetMapping("/health")
    public ApiResponse<String> healthCheck() {
        log.info("Processing health check request");
        
        try {
            // 尝试连接邮件服务器
            mailProviderFactory.getMailProvider().getLatestEmails();
            return ApiResponse.success("Health check successful, mail server connection established", null);
        } catch (Exception e) {
            log.error("Health check failed: {}", e.getMessage(), e);
            return ApiResponse.error("Health check failed, unable to connect to mail server: " + e.getMessage());
        }
    }
}