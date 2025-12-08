package com.example.welperback.dto.intake;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class IntakeRecordUpdateRequest {
    
    private String referralSource;
    
    private String initialNeedsSummary;
    
    private LocalDateTime interviewDate;
    
    private String interviewType;
    
    // JSONB fields
    private Map<String, Object> disabilityStatus;
    
    private Map<String, Object> familyMembers;
    
    private Map<String, Object> needsCategories;
    
    private String intakeResult; // SELECTED, NOT_SELECTED
}
