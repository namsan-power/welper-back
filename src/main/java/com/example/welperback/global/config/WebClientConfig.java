package com.example.welperback.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient aiWebClient() {
        return WebClient.builder()
                // ✅ AI 서버 주소 (나중에 실제 주소로 바꾸면 됨)
                .baseUrl("http://localhost:5000")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
