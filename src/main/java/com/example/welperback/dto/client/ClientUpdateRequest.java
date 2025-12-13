package com.example.welperback.dto.client;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClientUpdateRequest {
    
    private LocalDate registrationDate;
    
    private String clientName;
    
    @Pattern(regexp = "^(남|여)$", message = "성별은 '남' 또는 '여'만 가능합니다.")
    private String gender;
    
    private LocalDate birthDate;
    
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", 
            message = "연락처 형식이 올바르지 않습니다. (예: 010-1234-5678)")
    private String contactNumber;
    
    private String address;
    
    private String referralSource;
    
    private String initialNeedsSummary;
    
    private String assignedManagerId;
}
