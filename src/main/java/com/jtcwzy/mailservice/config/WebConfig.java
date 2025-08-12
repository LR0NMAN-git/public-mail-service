package com.jtcwzy.mailservice.config;

import com.jtcwzy.mailservice.interceptor.PasswordInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    
    private final PasswordInterceptor passwordInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(passwordInterceptor)
                .addPathPatterns("/**")  // 拦截所有请求
                .excludePathPatterns("/", "/index.html", "/js/**", "/css/**", "/images/**", "/**/*.js", "/**/*.css", "/**/*.png", "/**/*.jpg", "/**/*.jpeg", "/**/*.gif", "/**/*.svg");  // 排除首页和所有静态资源
    }
}