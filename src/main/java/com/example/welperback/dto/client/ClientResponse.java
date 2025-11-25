package com.example.welperback.dto.client;

import com.example.welperback.domain.client.Client;
import com.example.welperback.domain.client.ClientSex;
import com.example.welperback.domain.client.ClientStatus;
import com.example.welperback.domain.client.ReferralSource;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@Builder
public class ClientResponse {
    private Long clientId;
    private String name;
    private LocalDate birthDate;
    private String phoneNumber;
    private String address;
    private ClientSex sex;
    private ReferralSource referralSource;
    private ClientStatus status;
    private String requestContent;
    private LocalDate registrationDate;

    public static ClientResponse from(Client client) {
        return ClientResponse.builder()
                .clientId(client.getId())
                .name(client.getName())
                .birthDate(client.getBirthDate())
                .phoneNumber(client.getPhoneNumber())
                .address(client.getAddress())
                .sex(client.getSex())
                .referralSource(client.getReferralSource())
                .status(client.getStatus())
                .requestContent(client.getRequestContent())
                .registrationDate(client.getRegistrationDate())
                .build();
    }
}
