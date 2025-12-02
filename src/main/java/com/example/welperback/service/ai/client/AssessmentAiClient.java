package com.example.welperback.service.ai.client;

import com.example.welperback.dto.ai.AssessmentAiRequestDto;
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
 * 실제 AI 서버(노트북에서 돌리는 Python 등)와 통신하는 클라이언트.
 *
 * - HTTP 기반 예시
 * - 필요하면 나중에 ProcessBuilder 등으로 교체 가능.
 */
@Component
@RequiredArgsConstructor
public class AssessmentAiClient {

    @Qualifier("assessmentAiWebClient")
    private final WebClient aiWebClient;

    /**
     * AI 서버에 사정 요청을 보내고, 결과 JSON을 Map 구조로 받아온다.
     * 분석 시간이 길 수 있어 타임아웃은 넉넉하게 설정.
     */
    public Map<String, Object> callAssessment(AssessmentAiRequestDto dto) {

        try {
            return aiWebClient.post()
                    .uri("v1/analyze/first-report") // ★ 실제 AI 서버 엔드포인트에 맞게 수정
                    .bodyValue(dto)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .map(body -> new RuntimeException(
                                            "AI 서버 에러: " + clientResponse.statusCode() + " - " + body
                                    ))
                    )
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block(Duration.ofMinutes(30)); // GPU 따라 5분 이상 걸릴 수도 있으니 충분히
        } catch (WebClientResponseException e) {
            throw new RuntimeException(
                    "AI 서버 HTTP 응답 오류: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("AI 서버 호출 중 예외 발생: " + e.getMessage(), e);
        }
    }
}
