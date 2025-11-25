package com.example.welperback.dto.client.response;

import com.example.welperback.domain.client.Client;
import lombok.Getter;

@Getter
public class ClientDetailResponse {
    private Long clientId;
    private String name;
    private String phoneNumber;
    private String address;
    private String status;
    private String requestContent;

    public static ClientDetailResponse fromEntity(Client client) {
        ClientDetailResponse dto = new ClientDetailResponse();
        dto.clientId = client.getId();
        dto.name = client.getName();
        dto.phoneNumber = client.getPhoneNumber();
        dto.address = client.getAddress();
        dto.status = client.getStatus().name();
        dto.requestContent = client.getRequestContent();
        return dto;
    }
}
