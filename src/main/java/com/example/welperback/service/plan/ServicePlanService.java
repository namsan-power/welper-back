package com.example.welperback.service.plan;

import com.example.welperback.domain.assessment.ServicePlan;
import com.example.welperback.domain.client.Client;
import com.example.welperback.dto.plan.ServicePlanResponse;
import com.example.welperback.dto.plan.ServicePlanSaveRequest;
import com.example.welperback.dto.plan.ServicePlanUpdateRequest;
import com.example.welperback.repository.ClientRepository;
import com.example.welperback.repository.ServicePlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServicePlanService {

    private final ServicePlanRepository servicePlanRepository;
    private final ClientRepository clientRepository;

    public ServicePlanResponse save(ServicePlanSaveRequest dto) {

        Client client = clientRepository.findById(dto.caseNumber())
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 사례입니다.")
                );

        ServicePlan plan = ServicePlan.builder()
                .planId("SP-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10))
                .client(client)
                .planDate(dto.planDate())
                .planItems(dto.planItems())
                .supervisorFeedback(dto.supervisorFeedback())
                .contractFilePath(dto.contractFilePath())
                .build();

        return ServicePlanResponse.from(
                servicePlanRepository.save(plan)
        );
    }

    public ServicePlanResponse get(String planId) {
        return servicePlanRepository.findById(planId)
                .map(ServicePlanResponse::from)
                .orElseThrow(() ->
                        new IllegalArgumentException("계획서를 찾을 수 없습니다.")
                );
    }

    public List<ServicePlanResponse> getByCase(String caseNumber) {
        return servicePlanRepository
                .findByClient_CaseNumberOrderByPlanDateDesc(caseNumber)
                .stream()
                .map(ServicePlanResponse::from)
                .toList();
    }
    // ✅ UPDATE
    public ServicePlanResponse update(String planId, ServicePlanUpdateRequest dto) {
        ServicePlan plan = servicePlanRepository.findById(planId)
                .orElseThrow(() ->
                        new IllegalArgumentException("계획서를 찾을 수 없습니다.")
                );

        plan.setPlanDate(dto.planDate());
        plan.setPlanItems(dto.planItems());
        plan.setSupervisorFeedback(dto.supervisorFeedback());
        plan.setContractFilePath(dto.contractFilePath());

        return ServicePlanResponse.from(servicePlanRepository.save(plan));
    }

    public void delete(String planId) {
        servicePlanRepository.deleteById(planId);
    }
}
