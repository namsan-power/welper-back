package com.example.welperback.global.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {

        // (★) "이 '전화기'는 '문자열'을 '보낼' 때, 'UTF-8'만 '사용'해라!"
        return builder
                .additionalMessageConverters(
                        new StringHttpMessageConverter(StandardCharsets.UTF_8),

                        new AllEncompassingFormHttpMessageConverter()
                )
                .build();
    }
}