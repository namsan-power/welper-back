package com.example.welperback.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
// CORS 등 전역 설정
// 기본 세팅만 되어 있음
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173", "https://welper.store") // 프론트 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true) //쿠키 허용
                .allowedHeaders("*") //헤더에 토큰 허용
                .maxAge(3600); // 브라우저 검사 시간 1시간
    }
}
