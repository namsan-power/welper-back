package com.example.welperback.repository;

import com.example.welperback.domain.assessment.ServicePlan;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface ServicePlanRepository extends JpaRepository<ServicePlan, String> {

    List<ServicePlan> findByClient_CaseNumberOrderByPlanDateDesc(String caseNumber);
}
