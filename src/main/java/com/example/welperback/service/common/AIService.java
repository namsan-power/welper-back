package com.example.welperback.service.common;

import com.example.welperback.dto.report.response.AIAnalysisResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Async
@Service
@RequiredArgsConstructor
public class AIService {

    private final RestTemplate restTemplate;

    @Value("${ai.model.url}")
    private String aiModelUrl;

    public void analyzeVoice(byte[] voiceFileBytes, String originalFileName, String callbackUrl, Long reportId) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        ByteArrayResource fileResource = new ByteArrayResource(voiceFileBytes) {
            @Override
            public String getFilename() {
                return originalFileName;
            }
        };

        body.add("voiceFile", fileResource);
        body.add("callbackUrl", callbackUrl);
        body.add("reportId", reportId.toString());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String finalAiModelUrl = aiModelUrl + "/api/v1/analyze/voice/" + reportId;

        System.out.println("--- [AIService 로그] (비동기) ---");
        System.out.println("AI 모델에 '분석 시작' 요청: " + finalAiModelUrl);

        try {
            restTemplate.exchange(
                    finalAiModelUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            System.out.println("AI 모델에 '요청 전송' 성공!");

        } catch (Exception e) {
            System.err.println("AI 모델 통신 실패 (비동기): " + e.getMessage());
        }
    }
}