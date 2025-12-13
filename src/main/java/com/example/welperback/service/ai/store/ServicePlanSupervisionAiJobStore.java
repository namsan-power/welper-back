package com.example.welperback.service.ai.store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI 계획서 슈퍼비전 상태관리 서비스
 */
@Component
public class ServicePlanSupervisionAiJobStore {

    private final Map<String, SupervisionJob> store = new ConcurrentHashMap<>();

    public static final String STATUS_QUEUED = "QUEUED";
    public static final String STATUS_PROCESSING = "PROCESSING";
    public static final String STATUS_FINISHED = "FINISHED";
    public static final String STATUS_FAILED = "FAILED";

    /**
     * 슈퍼비전 작업 초기화 (큐 대기 상태)
     */
    public void initJob(String caseNumber, Map<String, Object> requestPayload) {

        SupervisionJob job = SupervisionJob.builder()
                .caseNumber(caseNumber)
                .status(STATUS_QUEUED)
                .errorMessage(null)
                .requestPayload(requestPayload)
                .planSupervision(null)
                .build();

        store.put(caseNumber, job);
    }

    public SupervisionJob getJob(String caseNumber) {
        return store.get(caseNumber);
    }

    /**
     * 실제 AI 작업 시작
     */
    public void markRunning(String caseNumber) {
        store.computeIfPresent(caseNumber, (key, job) ->
                job.toBuilder()
                        .status(STATUS_PROCESSING)
                        .build()
        );
    }

    /**
     * 작업 완료
     */
    public void markFinished(String caseNumber, String supervisionResult) {
        store.computeIfPresent(caseNumber, (key, job) ->
                job.toBuilder()
                        .status(STATUS_FINISHED)
                        .planSupervision(supervisionResult)
                        .errorMessage(null)
                        .build()
        );
    }

    /**
     * 작업 실패
     */
    public void markFailed(String caseNumber, String errorMessage) {
        store.computeIfPresent(caseNumber, (key, job) ->
                job.toBuilder()
                        .status(STATUS_FAILED)
                        .errorMessage(errorMessage)
                        .build()
        );
    }

    @Getter
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupervisionJob {

        private String caseNumber;

        /**
         * QUEUED / PROCESSING / FINISHED / FAILED
         */
        private String status;

        private String errorMessage;

        /**
         * 사용자 수정 계획서 payload
         */
        private Map<String, Object> requestPayload;

        /**
         * AI 슈퍼비전 결과 (텍스트)
         */
        private String planSupervision;
    }
}
