package com.example.welperback.controller.auth;

import com.example.welperback.dto.auth.*;
import com.example.welperback.global.response.ApiResponse;
import com.example.welperback.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
        summary = "사용자 로그인", 
        description = "userId와 password로 로그인하여 accessToken과 refreshToken을 발급받습니다."
    )
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/refresh")
    @Operation(
        summary = "토큰 갱신", 
        description = "refreshToken으로 새로운 accessToken과 refreshToken을 발급받습니다."
    )
    public ApiResponse<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        LoginResponse response = authService.refreshToken(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/logout")
    @Operation(
        summary = "로그아웃", 
        description = "현재 사용자의 refreshToken을 삭제하여 로그아웃합니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<String> logout() {
        authService.logout();
        return ApiResponse.success("로그아웃되었습니다.");
    }

    @PostMapping("/register")
    @Operation(
        summary = "사용자 등록", 
        description = "새로운 사용자를 등록합니다."
    )
    public ApiResponse<UserDto> register(@Valid @RequestBody RegisterRequest request) {
        UserDto userDto = authService.register(request);
        return ApiResponse.success(userDto);
    }

    @PostMapping("/change-password")
    @Operation(
        summary = "비밀번호 변경", 
        description = "현재 로그인한 사용자의 비밀번호를 변경합니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ApiResponse.success("비밀번호가 변경되었습니다.");
    }

    @GetMapping("/me")
    @Operation(
        summary = "현재 사용자 정보 조회", 
        description = "현재 로그인한 사용자의 정보를 조회합니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<UserDto> getCurrentUser() {
        UserDto userDto = authService.getCurrentUser();
        return ApiResponse.success(userDto);
    }
}

