package com.example.welperback.repository;

import com.example.welperback.domain.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, String> {
    
    // 모든 활성 클라이언트 조회 (삭제되지 않은)
    List<Client> findAllByDeletedAtIsNullOrderByRegistrationDateDesc();
    
    // 특정 담당자의 활성 클라이언트 조회
    List<Client> findByAssignedManagerUserIdAndDeletedAtIsNullOrderByRegistrationDateDesc(String userId);
    
    // 사례번호로 활성 클라이언트 조회
    Optional<Client> findByCaseNumberAndDeletedAtIsNull(String caseNumber);
    
    // 해당 연도의 최신 사례번호 조회 (자동 생성용)
    @Query("SELECT c.caseNumber FROM Client c WHERE c.caseNumber LIKE :yearPrefix% ORDER BY c.caseNumber DESC")
    Optional<String> findLatestCaseNumberByYear(@Param("yearPrefix") String yearPrefix);
}
