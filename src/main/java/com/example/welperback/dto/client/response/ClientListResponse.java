package com.example.welperback.dto.client.response;

import com.example.welperback.domain.client.Client;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ClientListResponse {
    private Long clientId;
    private String name;
    private LocalDate birthDate;
    private String phoneNumber;
    private String status;

    public static ClientListResponse fromEntity(Client client) {
        ClientListResponse dto = new ClientListResponse();
        dto.clientId = client.getId();
        dto.name = client.getName();
        dto.birthDate = client.getBirthDate();
        dto.phoneNumber = client.getPhoneNumber();
        dto.status = client.getStatus().name();
        return dto;
    }
}
