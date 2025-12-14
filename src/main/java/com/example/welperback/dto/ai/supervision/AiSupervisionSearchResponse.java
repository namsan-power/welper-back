package com.example.welperback.dto.ai.supervision;

import com.example.welperback.dto.assessment.ServicePlanDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AiSupervisionSearchResponse {
    private String caseNumber;
    private Double score;
    private ServicePlanDto servicePlan;
}

