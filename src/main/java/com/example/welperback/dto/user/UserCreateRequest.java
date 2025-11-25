package com.example.welperback.dto.user;

import com.example.welperback.domain.user.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserCreateRequest {
    private String userId;
    private String password;
    private String name;
    private Role role;
    private String agencyName;
    private String licenseNumber;
}
