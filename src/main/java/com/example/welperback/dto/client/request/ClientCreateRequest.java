package com.example.welperback.dto.client.request;

import com.example.welperback.domain.client.ReferralSource;
import com.example.welperback.domain.client.Sex;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ClientCreateRequest {
    private String name;
    private String birthDate;   // "1999-05-21"
    private String phoneNumber;
    private String address;
    private Sex sex;         // "남" or "여"
    private ReferralSource referralSource;
    private String requestContent;
}
