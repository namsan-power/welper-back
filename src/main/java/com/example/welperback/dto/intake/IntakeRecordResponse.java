package com.example.welperback.dto.intake;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntakeRecordResponse {
    
    private String recordId;
    
    private String caseNumber;
    
    private String clientName;
    
    private String receptionistId;
    
    private String referralSource;
    
    private String initialNeedsSummary;
    
    private LocalDateTime interviewDate;
    
    private String interviewType;
    
    // JSONB fields
    private Map<String, Object> disabilityStatus;
    
    private Map<String, Object> familyMembers;
    
    private Map<String, Object> needsCategories;
    
    private String intakeResult;
}
