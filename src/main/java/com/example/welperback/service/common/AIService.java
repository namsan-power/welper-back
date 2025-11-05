package com.example.welperback.service.common;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.example.welperback.dto.report.response.AIAnalysisResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AIService {

    private final RestTemplate restTemplate;

    @Value("${ai.model.url}")
    private String aiModelUrl; //(POST /api/v1/analyze/voice/{reportId}

    /**
     * AI 모델에 voice 분석 요청
     * callbackUrl 사용하여 비동기적으로 상태 반환
     * @param voiceFile
     * @param callbackUrl
     */
    @Async
    public void analyzeVoice(MultipartFile voiceFile, String callbackUrl){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        try{
            Resource fileResource = voiceFile.getResource();
            body.add("voiceFile", fileResource);
            body.add("callbackUrl", callbackUrl);
        } catch(Exception e){
            throw new RuntimeException(e);
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        System.out.println("------[AI 서비스 임시 로그]-----------");
        System.out.println("AI 모델에 분석 시작 요청: " + aiModelUrl);
        System.out.println("콜백 주소 전달: " +  callbackUrl);

        try{
            restTemplate.exchange(
                    aiModelUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            System.out.println("AI 모델에 요청 전송 성공!");
        } catch(Exception e){
            System.err.println("AI 모델 통신 실패: " + e.getMessage());
        }
    }

    public AIAnalysisResponseDto analyzeVoice(MultipartFile voiceFile) {
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        
        try {
            Resource fileResource = voiceFile.getResource();
            body.add("voiceFile", fileResource);
        } catch (Exception e) {
            throw new RuntimeException("파일을 읽는 데 실패했습니다.", e);
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        System.out.println("--- [AIService 로그] AI 모델에 분석 요청 ---");
        
        try {
            ResponseEntity<AIAnalysisResponseDto> response = restTemplate.postForEntity(
                aiModelUrl,        
                requestEntity,     
                AIAnalysisResponseDto.class
            );
            
            System.out.println("AI 모델 응답 성공!");
            return response.getBody(); 

        } catch (Exception e) {
            System.err.println("AI 모델 통신 실패: " + e.getMessage());
            // (나중엔 여기서 '커스텀 예외'를 던져야 함)
            return new AIAnalysisResponseDto(); 
        }
    }
}