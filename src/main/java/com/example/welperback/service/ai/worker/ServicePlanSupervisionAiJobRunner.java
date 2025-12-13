package com.example.welperback.service.ai.worker;

import com.example.welperback.service.ai.client.ServicePlanAiClient;
import com.example.welperback.service.ai.store.ServicePlanSupervisionAiJobStore;
import com.example.welperback.service.ai.store.ServicePlanSupervisionAiJobStore.SupervisionJob;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@RequiredArgsConstructor
public class ServicePlanSupervisionAiJobRunner {

    private final ServicePlanSupervisionAiJobStore jobStore;
    private final ServicePlanAiClient aiClient;

    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    @PostConstruct
    public void startWorker() {

        Thread worker = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {

                String caseNumber = null;

                try {
                    caseNumber = queue.take();

                    SupervisionJob job = jobStore.getJob(caseNumber);
                    if (job == null) continue;

                    jobStore.markRunning(caseNumber);

                    Map<String, Object> payload = job.getRequestPayload();

                    // 🔥 AI 슈퍼비전 호출 (텍스트만)
                    String supervisionResult =
                            aiClient.callPlanSupervision(payload);

                    jobStore.markFinished(caseNumber, supervisionResult);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    if (caseNumber != null) {
                        jobStore.markFailed(caseNumber, e.getMessage());
                    }
                }
            }
        });

        worker.setDaemon(true);
        worker.setName("service-plan-supervision-ai-job-worker");
        worker.start();
    }

    public void submit(String caseNumber) {
        queue.offer(caseNumber);
    }
}

