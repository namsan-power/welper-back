package com.example.welperback.dto.client;

import com.example.welperback.domain.client.Client;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDetailResponse {
    
    // 클라이언트 기본 정보
    private String caseNumber;
    private String clientName;
    private LocalDate birthDate;
    private String gender;
    private String contactNumber;
    private LocalDate registrationDate;
    private String referralSource;
    private String address;
    private String assignedManagerId;
    private String assignedManagerName;
    private Boolean privacyConsent;
    private String initialNeedsSummary;
    private String caseStatus;
    
    public static ClientDetailResponse from(Client client, String initialNeedsSummary) {
        return ClientDetailResponse.builder()
                .caseNumber(client.getCaseNumber())
                .clientName(client.getClientName())
                .birthDate(client.getBirthDate())
                .gender(client.getGender())
                .contactNumber(client.getContactNumber())
                .registrationDate(client.getRegistrationDate())
                .address(client.getAddress())
                .assignedManagerId(client.getAssignedManager() != null ? 
                        client.getAssignedManager().getUserId() : null)
                .assignedManagerName(client.getAssignedManager() != null ? 
                        client.getAssignedManager().getName() : null)
                .privacyConsent(client.getPrivacyConsent())
                .caseStatus(client.getCaseStatus())
                .initialNeedsSummary(initialNeedsSummary)
                .build();
    }
    
    public static ClientDetailResponse from(Client client) {
        return from(client, null);
    }
}
