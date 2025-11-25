package com.example.welperback.dto.client;

import com.example.welperback.domain.client.ClientSex;
import com.example.welperback.domain.client.ReferralSource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ClientCreateRequest {
    private String name;
    private LocalDate birthDate;
    private String phoneNumber;
    private String address;
    private ClientSex sex;
    private ReferralSource referralSource;
    private String requestContent;
    private LocalDate registrationDate;
}
