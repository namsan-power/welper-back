package com.example.welperback.global.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient 공용 설정
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    /**
     * AI 사정 서버 호출용 WebClient.
     *
     * application.yml 예시:
     *
     * ai:
     *   assessment:
     *     base-url: http://localhost:9001   # 또는 ngrok 등 외부 주소
     */
    @Bean(name = "assessmentAiWebClient")
    public WebClient assessmentAiWebClient(
            WebClient.Builder builder,
            @Value("${ai.assessment.base-url}") String baseUrl
    ) {
        return builder
                .baseUrl(baseUrl == null ? "" : baseUrl)
                .build();
    }

    /**
     * AI 슈퍼비전 검색 서버 호출용 WebClient.
     */
    @Bean
    @Qualifier("supervisionAiWebClient")
    public WebClient supervisionAiWebClient(
            WebClient.Builder builder,
            @Value("${ai.supervision.base-url}") String baseUrl
    ) {
        return builder
                .baseUrl(baseUrl == null ? "" : baseUrl)
                .build();
    }
    @Bean(name = "planAiWebClient")
    public WebClient planAiWebClient(
            WebClient.Builder builder,
            @Value("${ai.assessment.base-url}") String baseUrl){
        return builder.baseUrl(baseUrl).build();
    }

}
