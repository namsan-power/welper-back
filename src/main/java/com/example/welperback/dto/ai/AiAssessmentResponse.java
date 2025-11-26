package com.example.welperback.dto.ai;

public record AiAssessmentResponse(
        String requestId,
        String status,
        String message
) {}
