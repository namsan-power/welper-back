package com.example.welperback.service;

import com.example.welperback.domain.user.User;
import com.example.welperback.dto.user.UserCreateRequest;
import com.example.welperback.dto.user.UserResponse;
import com.example.welperback.global.exception.CustomException;
import com.example.welperback.global.exception.ErrorCode;
import com.example.welperback.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserCreateRequest request, Long adminId) {
        if (userRepository.existsByEmail(request.getUserId())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.builder()
                .email(request.getUserId())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(request.getRole())
                .agencyName(request.getAgencyName())
                .licenseNumber(request.getLicenseNumber())
                .createdByAdminId(adminId)
                .build();

        userRepository.save(user);
        return UserResponse.from(user);
    }
}
