package com.example.welperback.dto.ai;

import lombok.Builder;

import java.util.Map;

@Builder
public record AiServicePlanStatusResponse(
        String caseNumber,
        String status,     // NONE / PROCESSING / FINISHED / FAILED
        String message,
        Map<String, Object> planDraft  // AI가 준 초안 JSON
) {}
