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
public class ClientListDto {
    
    private String caseNumber;
    private String clientName;
    private LocalDate birthDate;
    private String gender;
    private String contactNumber;
    private String address;
    private String assignedManagerName;
    private String caseStatus;
    
    public static ClientListDto from(Client client) {
        return ClientListDto.builder()
                .caseNumber(client.getCaseNumber())
                .clientName(client.getClientName())
                .birthDate(client.getBirthDate())
                .gender(client.getGender())
                .contactNumber(client.getContactNumber())
                .address(client.getAddress())
                .assignedManagerName(client.getAssignedManager() != null ? 
                        client.getAssignedManager().getName() : null)
                .caseStatus(client.getCaseStatus())
                .build();
    }
}
