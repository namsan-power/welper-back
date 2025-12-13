package com.example.welperback.dto.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Map;

public record ServicePlanSupervisionRequestDto(

        @NotBlank
        String caseNumber,

        LocalDate planeDate,
        /**
         * 사용자가 직접 수정한 계획서 내용
         * (AI 초안 + 사람 수정 결과)
         */
        @NotNull
        Map<String, Object> planItems
) {}
