package com.example.welperback.dto.client;

import com.example.welperback.domain.client.ClientStatus;
import com.example.welperback.domain.client.ReferralSource;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ClientUpdateRequest {
    private String name;
    private String phoneNumber;
    private String address;
    private ClientStatus status;
    private ReferralSource referralSource;
    private String requestContent;
}
