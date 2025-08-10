package com.jtcwzy.mailservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class ControllerLogAspect {

    @Pointcut("execution(* com.jtcwzy.mailservice.controller..*(..))")
    public void controllerPointcut() {}

    @Around("controllerPointcut()")
    public Object logControllerExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        String url = request.getRequestURL().toString();
        String method = request.getMethod();
        String ip = getClientIpAddress(request);
        Map<String, String> headers = getHeaders(request);

        // 记录请求开始日志
        log.info("Starting controller execution: {} {} from IP: {}", method, url, ip);
        log.info("Request headers: {}", headers);

        long startTime = System.currentTimeMillis();
        Object result = null;
        Throwable throwable = null;
        
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable t) {
            throwable = t;
            throw t;
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // 记录请求结束日志
            if (throwable != null) {
                log.error("Controller execution failed: {} {} from IP: {} after {}ms with exception: {}", method, url, ip, duration, throwable.getMessage());
            } else {
                HttpServletResponse response = attributes.getResponse();
                int status = response != null ? response.getStatus() : 200;
                log.info("Finished controller execution: {} {} from IP: {} with status: {} in {}ms", method, url, ip, status, duration);
            }
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        return headers;
    }
}