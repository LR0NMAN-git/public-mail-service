package com.jtcwzy.mailservice.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String time;
    
    public static <T> ApiResponse<T> success(String message, T data) {
        String formattedTimestamp = Instant.now().atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return new ApiResponse<>(true, message, data, formattedTimestamp);
    }
    
    public static <T> ApiResponse<T> error(String message) {
        String formattedTimestamp = Instant.now().atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return new ApiResponse<>(false, message, null, formattedTimestamp);
    }
}