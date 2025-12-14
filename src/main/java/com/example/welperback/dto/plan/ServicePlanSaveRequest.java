package com.example.welperback.dto.plan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Map;

public record ServicePlanSaveRequest(

        @NotBlank
        String caseNumber,

        @NotNull
        LocalDate planDate,

        @NotNull
        Map<String, Object> planItems,

        String supervisorFeedback,   // ← AI 슈퍼비전 결과

        String contractFilePath
) {}
