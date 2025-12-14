package com.example.welperback.dto.assessment;

import com.example.welperback.domain.assessment.ServicePlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class ServicePlanDto {

    private String planId;
    private String caseNumber;
    private LocalDate planDate;
    private Map<String, Object> planItems;
    private String contractFilePath;
    private String supervisorFeedback;

    public static ServicePlanDto from(ServicePlan plan) {
        return ServicePlanDto.builder()
                .planId(plan.getPlanId())
                .caseNumber(plan.getClient() != null ? plan.getClient().getCaseNumber() : null)
                .planDate(plan.getPlanDate())
                .planItems(plan.getPlanItems())
                .contractFilePath(plan.getContractFilePath())
                .supervisorFeedback(plan.getSupervisorFeedback())
                .build();
    }
}

