package com.example.welperback.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 특정 사례(caseNumber)에 대한
 * AI 사정 작업의 상태 & 결과 응답 DTO
 *
 * - caseNumber : 사례 번호 (Client.caseNumber)
 * - status     : NONE / PROCESSING / FINISHED / FAILED
 * - message    : 상태 설명 메시지
 * - assessment : AI가 생성한 사정 JSON (FINISHED일 때)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAssessmentStatusResponse {

    private String caseNumber;
    private String status;
    private String message;
    private Map<String, Object> assessment;
}
