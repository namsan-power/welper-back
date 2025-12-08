package com.example.welperback.repository;

import com.example.welperback.domain.client.IntakeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IntakeRecordRepository extends JpaRepository<IntakeRecord, String> {
    
    /**
     * 사례번호로 인테이크 기록 조회
     */
    Optional<IntakeRecord> findByClient_CaseNumber(String caseNumber);
    
    /**
     * 사례번호로 모든 인테이크 기록 조회 (1:1이지만 리스트로 반환)
     */
    List<IntakeRecord> findAllByClient_CaseNumber(String caseNumber);
}
