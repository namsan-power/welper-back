package com.example.welperback.repository.report;

import com.example.welperback.domain.report.ReportClient;
import com.example.welperback.domain.report.ReportClientId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportClientRepository extends JpaRepository<ReportClient, ReportClientId> {
}