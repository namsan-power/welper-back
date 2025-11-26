package com.example.welperback.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAssessmentStatusResponse {

    // PROCESSING / FINISHED
    private String status;

    // PROCESSING일 때: "AI가 음성 분석을 진행 중입니다."
    // FINISHED일 때: null 가능
    private String message;

    // FINISHED일 때만 채워지는 평가 결과 전체 JSON
    private Map<String, Object> assessment;
}
