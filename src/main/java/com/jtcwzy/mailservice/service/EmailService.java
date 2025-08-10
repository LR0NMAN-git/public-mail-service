package com.jtcwzy.mailservice.service;

import com.jtcwzy.mailservice.dto.Email;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {

    private final MailProviderFactory mailProviderFactory;

    public List<Email> getLatestEmails() throws MessagingException {
        log.info("Fetching latest emails");
        List<Email> emails = mailProviderFactory.getMailProvider().getLatestEmails();
        log.info("Successfully fetched {} emails", emails.size());
        return emails;
    }
}