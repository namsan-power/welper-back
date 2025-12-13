package com.example.welperback.dto.plan;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Map;

public record ServicePlanUpdateRequest(

        @NotNull
        LocalDate planDate,

        @NotNull
        Map<String, Object> planItems,

        String supervisorFeedback,

        String contractFilePath
) {}
