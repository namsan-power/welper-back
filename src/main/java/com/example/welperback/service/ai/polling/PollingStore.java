package com.example.welperback.service.ai.polling;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

//AiPollingService 는 지금은 안 써도 돼. 나중에 진짜 AI 서버 붙일 때 여기 PollingStore 대신 WebClient 호출로 갈아끼우면 됨.
@Component
public class PollingStore {

    // requestId -> 상태 저장
    private final Map<String, PollingStatus> store = new ConcurrentHashMap<>();

    public void init(String requestId) {
        store.put(requestId, new PollingStatus(0, "PROCESSING"));
    }

    public PollingStatus get(String requestId) {
        return store.get(requestId);
    }

    public void increaseProgress(String requestId) {
        PollingStatus status = store.get(requestId);
        if (status == null) return;

        int newProgress = Math.min(status.progress() + 25, 100);
        String newState = (newProgress >= 100) ? "FINISHED" : "PROCESSING";

        store.put(requestId, new PollingStatus(newProgress, newState));
    }

    public record PollingStatus(int progress, String state) {}
}
