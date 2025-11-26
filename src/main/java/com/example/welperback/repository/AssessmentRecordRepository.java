package com.example.welperback.repository;

import com.example.welperback.domain.assessment.AssessmentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssessmentRecordRepository extends JpaRepository<AssessmentRecord, String> {
}
