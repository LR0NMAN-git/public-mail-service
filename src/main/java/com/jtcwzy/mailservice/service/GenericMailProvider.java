package com.jtcwzy.mailservice.service;

import com.jtcwzy.mailservice.dto.Email;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMultipart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Slf4j
@Service
public class GenericMailProvider implements MailProvider {

    @Value("${mail.server.host}")
    private String host;

    @Value("${mail.server.port}")
    private String port;

    @Value("${mail.server.protocol}")
    private String protocol;

    @Value("${mail.server.username}")
    private String username;

    @Value("${mail.server.password}")
    private String password;

    @Value("${mail.server.folder}")
    private String folder;

    @Value("${mail.server.readtimeout}")
    private int readTimeout;

    @Override
    public List<Email> getLatestEmails() throws MessagingException {
        log.info("Fetching latest emails from {}", host);
        log.debug("Starting GenericMailProvider.getLatestEmails method");
        List<Email> emails = new ArrayList<>();
        
        try {
            // 设置邮箱的连接属性
            log.debug("Setting up mail connection properties");
            Properties properties = new Properties();
            properties.put("mail.store.protocol", protocol);
            properties.put("mail." + protocol + ".host", host);
            properties.put("mail." + protocol + ".port", port);
            
            // 添加超时属性
            properties.put("mail." + protocol + ".timeout", String.valueOf(readTimeout));
            properties.put("mail." + protocol + ".connectiontimeout", String.valueOf(readTimeout));
            
            // 添加SSL属性
            properties.put("mail." + protocol + ".ssl.enable", "true");
            properties.put("mail." + protocol + ".ssl.trust", "*");

            // 创建会话
            log.debug("Creating email session with properties");
            Session emailSession = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    log.debug("Authenticating with username: {}", username);
                    return new PasswordAuthentication(username, password);
                }
            });
            
            log.debug("Creating email session with SSL enabled");

            // 连接到邮箱服务器
            log.debug("Connecting to mail server with username: {}", username);
            Store store = emailSession.getStore(protocol);
            log.debug("Attempting to connect to {}", host);
            try {
                store.connect();
                log.debug("Connected to mail server successfully");
            } catch (Exception e) {
                log.error("Failed to connect to mail server: {}", e.getMessage(), e);
                throw e;
            }

            // 打开收件箱
            Folder emailFolder = store.getFolder(folder);
            emailFolder.open(Folder.READ_ONLY);

            // 获取最新的10封邮件
            Message[] messages = emailFolder.getMessages();
            int messageCount = messages.length;
            log.debug("Total messages in folder: {}", messageCount);
            int start = Math.max(0, messageCount - 10);
            Message[] latestMessages = new Message[Math.min(10, messageCount)];
            System.arraycopy(messages, start, latestMessages, 0, latestMessages.length);

            // 解析邮件内容
            for (Message message : latestMessages) {
                Email email = new Email();
                try {
                    email.setSubject(message.getSubject());
                    
                    // 解析发件人信息
                    Address[] fromAddresses = message.getFrom();
                    if (fromAddresses != null && fromAddresses.length > 0) {
                        InternetAddress internetAddress = (InternetAddress) fromAddresses[0];
                        String personal = internetAddress.getPersonal();
                        String address = internetAddress.getAddress();
                        
                        if (personal != null) {
                            email.setFrom(personal + " <" + address + ">");
                        } else {
                            email.setFrom(address);
                        }
                    } else {
                        email.setFrom("未知发件人");
                    }
                    
                    // 格式化时间
                    if (message.getSentDate() != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        email.setDate(sdf.format(message.getSentDate()));
                    } else {
                        email.setDate("未知时间");
                    }
                    
                    // 处理邮件内容
                    Object content = message.getContent();
                    if (content instanceof String) {
                        email.setContent((String) content);
                    } else if (content instanceof MimeMultipart) {
                        email.setContent(getTextFromMimeMultipart((MimeMultipart) content));
                    }
                } catch (IOException e) {
                    // 处理IO异常
                    log.warn("Failed to read email content: {}", e.getMessage());
                    email.setContent("无法读取邮件内容");
                }
                
                emails.add(email);
            }

            // 关闭连接
            emailFolder.close(false);
            store.close();
            
            // 按时间倒序排序
            Collections.sort(emails, new Comparator<Email>() {
                @Override
                public int compare(Email e1, Email e2) {
                    // 由于日期已经是格式化的字符串，我们需要解析回Date对象进行比较
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date1 = sdf.parse(e1.getDate());
                        Date date2 = sdf.parse(e2.getDate());
                        // 倒序排序，所以返回date2.compareTo(date1)
                        return date2.compareTo(date1);
                    } catch (Exception ex) {
                        // 如果解析失败，保持原有顺序
                        return 0;
                    }
                }
            });
            
            log.info("Successfully fetched {} emails from {}", emails.size(), host);
        } catch (Exception e) {
            log.error("Failed to fetch emails from {}: {}", host, e.getMessage(), e);
            throw new MessagingException("Failed to fetch emails from " + host, e);
        }

        return emails;
    }

    private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException {
        StringBuilder result = new StringBuilder();
        try {
            int count = mimeMultipart.getCount();
            for (int i = 0; i < count; i++) {
                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    result.append((String) bodyPart.getContent());
                    break;
                } else if (bodyPart.isMimeType("text/html")) {
                    String html = (String) bodyPart.getContent();
                    result.append(html);
                    break;
                } else if (bodyPart.getContent() instanceof MimeMultipart) {
                    result.append(getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent()));
                }
            }
        } catch (IOException e) {
            // 处理IO异常
            result.append("无法读取邮件内容");
        }
        return result.toString();
    }
}