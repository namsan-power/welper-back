package com.example.welperback.repository;

import com.example.welperback.domain.assessment.ServicePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServicePlanRepository extends JpaRepository<ServicePlan, String> {

    Optional<ServicePlan> findFirstByClient_CaseNumberOrderByPlanDateDesc(String caseNumber);
}

