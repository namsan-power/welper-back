package com.example.welperback.dto.client.request;

import com.example.welperback.domain.client.ClientStatus;
import com.example.welperback.domain.client.ReferralSource;
import com.example.welperback.domain.client.Sex;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ClientUpdateRequest {
    private String name;
    private String phoneNumber;
    private String address;
    private ClientStatus status;          // "상담중" / "솔루션 진행 중" / "종료"
    private ReferralSource referralSource;  // "온라인" / "전화" / "방문" / "기타"
    private String requestContent;
    private Sex sex;         // "남" or "여"

}
