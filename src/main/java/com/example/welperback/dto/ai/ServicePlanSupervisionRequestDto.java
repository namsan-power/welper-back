package com.example.welperback.dto.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Map;

public record ServicePlanSupervisionRequestDto(

        @NotBlank
        String caseNumber,

        @NotNull
        LocalDate planDate,

        @NotNull
        Map<String, Object> planItems
) {}
