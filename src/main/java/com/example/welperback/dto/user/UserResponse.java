package com.example.welperback.dto.user;

import com.example.welperback.domain.user.Role;
import com.example.welperback.domain.user.User;
import com.example.welperback.domain.user.UserStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private String userId;
    private String name;
    private Role role;
    private String agencyName;
    private String licenseNumber;
    private UserStatus status;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .agencyName(user.getAgencyName())
                .licenseNumber(user.getLicenseNumber())
                .status(user.getStatus())
                .build();
    }
}
