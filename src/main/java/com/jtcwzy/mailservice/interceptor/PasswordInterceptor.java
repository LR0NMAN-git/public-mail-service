package com.jtcwzy.mailservice.interceptor;

import com.jtcwzy.mailservice.config.PasswordConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Slf4j
@Component
public class PasswordInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // 设置响应内容类型
        response.setContentType("application/json;charset=UTF-8");
        
        // 获取Authorization头
        String authHeader = request.getHeader("Authorization");
        
        // 如果没有Authorization头，返回401
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\":false,\"message\":\"缺少认证信息\"}");
            return false;
        }
        
        // 提取token
        String token = authHeader.substring(7);
        
        // 获取配置中的加密密码
        String encryptedPassword = PasswordConfig.getEncryptedPassword();
        
        // 验证密码
        if (encryptedPassword == null || !encryptedPassword.equals(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\":false,\"message\":\"密码错误\"}");
            return false;
        }
        
        return true;
    }
}