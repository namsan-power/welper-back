package com.example.welperback.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AI 작업 등록 시 사용하는 간단 응답 DTO
 * - jobId  : 생성된 작업 ID
 * - status : QUEUED 등 초기 상태
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiJobResponse {

    private String jobId;
    private String status;
}
