package com.jtcwzy.mailservice.controller;

import com.jtcwzy.mailservice.dto.ApiResponse;
import com.jtcwzy.mailservice.dto.Email;
import com.jtcwzy.mailservice.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/emails")
public class EmailController {

    private final EmailService emailService;

    @GetMapping
    public ApiResponse<List<Email>> getEmails() throws MessagingException {
        log.info("Received request to get latest emails");
        List<Email> emails = emailService.getLatestEmails();
        log.info("Successfully retrieved {} emails", emails.size());
        
        return ApiResponse.success("Emails retrieved successfully", emails);
    }
}