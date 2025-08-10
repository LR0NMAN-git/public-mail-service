package com.jtcwzy.mailservice.dto;

import lombok.Data;

@Data
public class Email {
    private String subject;
    private String from;
    private String date;
    private String content;
}