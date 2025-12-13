package com.example.welperback.service.ai.worker;

import com.example.welperback.service.ai.client.ServicePlanAiClient;
import com.example.welperback.service.ai.store.ServicePlanAiJobStore;
import com.example.welperback.service.ai.store.ServicePlanAiJobStore.AiJob;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@RequiredArgsConstructor
public class ServicePlanAiJobRunner {

    private final ServicePlanAiJobStore jobStore;
    private final ServicePlanAiClient aiClient;

    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    @PostConstruct
    public void startWorker() {
        Thread worker = new Thread(() -> {
            while (true) {
                String caseNumber = null;
                try {
                    caseNumber = queue.take();

                    AiJob job = jobStore.getJob(caseNumber);
                    if (job == null) continue;

                    jobStore.markRunning(caseNumber);

                    Map<String, Object> payload = job.getRequestPayload();

                    Map<String, Object> planDraft = aiClient.callPlanDraft(payload);

                    jobStore.markFinished(caseNumber, planDraft);

                } catch (Exception e) {
                    e.printStackTrace();
                    if (caseNumber != null) {
                        jobStore.markFailed(caseNumber, e.getMessage());
                    }
                }
            }
        });

        worker.setDaemon(true);
        worker.setName("service-plan-ai-job-worker");
        worker.start();
    }

    public void submit(String caseNumber) {
        queue.offer(caseNumber);
    }
}
