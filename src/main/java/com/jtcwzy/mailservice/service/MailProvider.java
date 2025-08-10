package com.jtcwzy.mailservice.service;

import com.jtcwzy.mailservice.dto.Email;
import jakarta.mail.MessagingException;
import java.util.List;

public interface MailProvider {
    List<Email> getLatestEmails() throws MessagingException;
}