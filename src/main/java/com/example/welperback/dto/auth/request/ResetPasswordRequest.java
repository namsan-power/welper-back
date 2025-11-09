package com.example.welperback.dto.auth.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter // getter
@NoArgsConstructor // 생성자
public class ResetPasswordRequest {
    private String email;
}
