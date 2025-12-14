package com.example.welperback.service.ai.store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 *  AI 계획 생성 상태관리 서비스
 */
@Component
public class ServicePlanAiJobStore {

    private final Map<String, AiJob> store = new ConcurrentHashMap<>();

    public void initJob(String caseNumber, Map<String, Object> requestPayload) {
        AiJob job = AiJob.builder()
                .caseNumber(caseNumber)
                .status("PROCESSING")
                .errorMessage(null)
                .requestPayload(requestPayload)
                .planDraft(null)
                .build();
        store.put(caseNumber, job);
    }

    public AiJob getJob(String caseNumber) {
        return store.get(caseNumber);
    }

    public void markRunning(String caseNumber) {
        store.computeIfPresent(caseNumber, (key, job) ->
                job.toBuilder().status("PROCESSING").build()
        );
    }

    public void markFinished(String caseNumber, Map<String, Object> planDraft) {
        store.computeIfPresent(caseNumber, (key, job) ->
                job.toBuilder()
                        .status("FINISHED")
                        .planDraft(planDraft)
                        .errorMessage(null)
                        .build()
        );
    }
    public void markFinishedWithComment(String caseNumber, String comment) {
        store.computeIfPresent(caseNumber, (key, job) ->
                job.toBuilder()
                        .status("FINISHED")
                        .planDraftComment(comment)
                        .errorMessage(null)
                        .build()
        );
    }

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
        private String status; // PROCESSING / FINISHED / FAILED
        private String errorMessage;
        private Map<String, Object> requestPayload;
        private Map<String, Object> planDraft;
        private String planDraftComment;
    }
}
