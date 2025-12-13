package com.example.welperback.dto.ai;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ServicePlanSupervisionResponse {

    private String caseNumber;
    private String status;        // PROCESSING / FINISHED / FAILED / NONE
    private String message;
    private String planSupervision;
}