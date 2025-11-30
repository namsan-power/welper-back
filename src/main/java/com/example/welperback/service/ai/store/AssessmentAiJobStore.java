package com.example.welperback.service.ai.store;

import com.example.welperback.dto.ai.AssessmentAiRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI 사정 작업(Job)의 상태/결과를 관리하는 메모리 스토어.
 *
 * - key는 caseNumber(사례번호) 기준
 * - 내부 상태(status)는 PROCESSING, FINISHED, FAILED 정도만 사용
 *
 *  ※ 나중에 필요하면 이 구조를 RDB 테이블로 승격하면 됨.
 */
@Component
public class AssessmentAiJobStore {

    private final Map<String, AiJob> store = new ConcurrentHashMap<>();

    /**
     * 새로운 사례에 대한 AI Job 초기화
     */
    public void initJob(String caseNumber, AssessmentAiRequestDto request) {
        AiJob job = AiJob.builder()
                .caseNumber(caseNumber)
                .status("PROCESSING")   // 요청 들어온 시점부터 FE 관점에선 "처리중"
                .errorMessage(null)
                .request(request)
                .assessment(null)
                .build();
        store.put(caseNumber, job);
    }

    /**
     * 현재 Job 조회
     */
    public AiJob getJob(String caseNumber) {
        return store.get(caseNumber);
    }

    /**
     * 워커가 실제 실행 중일 때 호출 (상태는 그대로 PROCESSING 유지)
     */
    public void markRunning(String caseNumber) {
        store.computeIfPresent(caseNumber, (key, job) ->
                job.toBuilder()
                        .status("PROCESSING")
                        .build()
        );
    }

    /**
     * AI 분석이 성공적으로 끝났을 때 호출
     */
    public void markFinished(String caseNumber, Map<String, Object> assessment) {
        store.computeIfPresent(caseNumber, (key, job) ->
                job.toBuilder()
                        .status("FINISHED")
                        .assessment(assessment)
                        .errorMessage(null)
                        .build()
        );
    }

    /**
     * AI 분석이 실패했을 때 호출
     */
    public void markFailed(String caseNumber, String errorMessage) {
        store.computeIfPresent(caseNumber, (key, job) ->
                job.toBuilder()
                        .status("FAILED")
                        .errorMessage(errorMessage)
                        .build()
        );
    }

    @Getter
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AiJob {
        private String caseNumber;
        private String status;                  // PROCESSING / FINISHED / FAILED
        private String errorMessage;
        private AssessmentAiRequestDto request; // AI 서버에 넘길 원본 요청
        private Map<String, Object> assessment; // AI 서버가 돌려준 사정 JSON
    }
}
