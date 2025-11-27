package com.example.welperback.dto.auth;

import com.example.welperback.domain.account.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserDto {
    private String userId;
    private String name;
    private String role;
    private String agencyName;
    private String status;

    public static UserDto from(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .role(user.getRole())
                .agencyName(user.getAgencyName())
                .status(user.getStatus())
                .build();
    }
}
