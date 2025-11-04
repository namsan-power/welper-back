package com.example.welperback.repository.report;

import com.example.welperback.domain.report.ReportParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportParticipantRepository extends JpaRepository<ReportParticipant, ReportParticipant.Pk> {
}