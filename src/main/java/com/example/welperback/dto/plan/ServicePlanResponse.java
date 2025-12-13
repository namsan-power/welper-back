package com.example.welperback.dto.plan;

import com.example.welperback.domain.assessment.ServicePlan;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Builder
public class ServicePlanResponse {

    private String planId;
    private String caseNumber;
    private LocalDate planDate;
    private Map<String, Object> planItems;
    private String supervisorFeedback;
    private String contractFilePath;

    public static ServicePlanResponse from(ServicePlan plan) {
        return ServicePlanResponse.builder()
                .planId(plan.getPlanId())
                .caseNumber(plan.getClient().getCaseNumber())
                .planDate(plan.getPlanDate())
                .planItems(plan.getPlanItems())
                .supervisorFeedback(plan.getSupervisorFeedback())
                .contractFilePath(plan.getContractFilePath())
                .build();
    }
}
