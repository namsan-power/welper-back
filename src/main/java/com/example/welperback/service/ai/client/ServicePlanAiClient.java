package com.example.welperback.service.ai.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.Map;
/**
 *  계획생성 : AI 서버 호출 서비스
 */
@Component
public class ServicePlanAiClient {

    private final WebClient aiWebClient;

    public ServicePlanAiClient(@Qualifier("planAiWebClient") WebClient aiWebClient) {
        this.aiWebClient = aiWebClient;
    }
    public String callPlanSupervision(Map<String, Object> payload) {
        return aiWebClient.post()
                .uri("/ai/plan/supervision")
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public Map<String, Object> callPlanDraft(Map<String, Object> payload) {
        try {
            return aiWebClient.post()
                    .uri("/v1/analyze/service-plan/draft") // ✅ AI 서버 엔드포인트에 맞게 수정
                    .bodyValue(payload)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .map(body -> new RuntimeException(
                                            "AI 서버 에러: " + clientResponse.statusCode() + " - " + body
                                    ))
                    )
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block(Duration.ofMinutes(30));
        } catch (WebClientResponseException e) {
            throw new RuntimeException(
                    "AI 서버 HTTP 응답 오류: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("AI 서버 호출 중 예외 발생: " + e.getMessage(), e);
        }
    }
}
