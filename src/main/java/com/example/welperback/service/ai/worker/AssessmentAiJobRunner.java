package com.example.welperback.service.ai.worker;

import com.example.welperback.dto.ai.AssessmentAiRequestDto;
import com.example.welperback.service.ai.client.AssessmentAiClient;
import com.example.welperback.service.ai.store.AssessmentAiJobStore;
import com.example.welperback.service.ai.store.AssessmentAiJobStore.AiJob;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * AI Job 큐를 소비하면서, 실제로 AI 서버를 호출하는 백그라운드 워커.
 *
 * - 큐에는 caseNumber만 들어감.
 * - 단일 스레드로 순차 처리 (GPU/노트북 부하 조절용).
 */
@Component
@RequiredArgsConstructor
public class AssessmentAiJobRunner {

    private final AssessmentAiJobStore jobStore;
    private final AssessmentAiClient aiClient;

    // caseNumber 큐
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    /**
     * 서버 시작 시 워커 스레드 1개 가동
     */
    @PostConstruct
    public void startWorker() {
        Thread worker = new Thread(() -> {
            while (true) {
                String caseNumber = null;
                try {
                    // 1) 큐에서 caseNumber 하나 가져옴 (블로킹)
                    caseNumber = queue.take();

                    AiJob job = jobStore.getJob(caseNumber);
                    if (job == null) {
                        continue;
                    }

                    // 2) 상태 RUNNING (외부에선 그냥 PROCESSING 으로 보임)
                    jobStore.markRunning(caseNumber);

                    AssessmentAiRequestDto request = job.getRequest();

                    // 3) 실제 AI 서버 호출 (여기서 5분짜리 연산 발생 가능)
                    Map<String, Object> assessment = aiClient.callAssessment(request);

                    // 4) 완료 처리
                    jobStore.markFinished(caseNumber, assessment);

                } catch (Exception e) {
                    e.printStackTrace();
                    if (caseNumber != null) {
                        jobStore.markFailed(caseNumber, e.getMessage());
                    }
                }
            }
        });

        worker.setDaemon(true);
        worker.setName("assessment-ai-job-worker");
        worker.start();
    }

    /**
     * AI Job 실행 요청 (큐에 caseNumber 등록)
     */
    public void submit(String caseNumber) {
        queue.offer(caseNumber);
    }
}
