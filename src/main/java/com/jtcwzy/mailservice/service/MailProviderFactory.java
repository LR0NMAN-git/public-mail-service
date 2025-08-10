package com.jtcwzy.mailservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class MailProviderFactory {

    private final GenericMailProvider genericMailProvider;

    @Value("${mail.provider:generic}")
    private String mailProvider;

    public MailProvider getMailProvider() {
        log.info("Getting mail provider for: {}", mailProvider);
        // 现在只支持通用邮件提供者
        log.debug("Returning GenericMailProvider");
        return genericMailProvider;
    }
}