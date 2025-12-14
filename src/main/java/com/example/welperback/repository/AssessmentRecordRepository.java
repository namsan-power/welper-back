package com.example.welperback.repository;

import com.example.welperback.domain.assessment.AssessmentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AssessmentRecordRepository extends JpaRepository<AssessmentRecord, String> {

    /**
     * 특정 사례(caseNumber)에 속한 모든 사정 기록 조회.
     * <p>
     * ERD 기준:
     * - AssessmentRecord.caseNumber -> Client.caseNumber (FK)
     * - 엔티티에서는 client 필드로 연결되어 있다고 가정.
     */
    List<AssessmentRecord> findByClient_CaseNumber(String caseNumber);

    /**
     * 1차 사정결과 1개 조회(최신 기준)
     */
    Optional<AssessmentRecord>
    findTopByClient_CaseNumberAndTypeOrderByAssessmentDateDesc(
            String caseNumber,
            String type
    );

}