package com.example.welperback.controller.auth;

import com.example.welperback.dto.auth.request.LoginRequest;
import com.example.welperback.dto.auth.response.LoginResponse;
import com.example.welperback.dto.auth.request.SignupRequest;
import com.example.welperback.global.response.ApiResponse;
import com.example.welperback.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.ok(ApiResponse.success("회원가입 성공", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // JWT 무효화 or 클라이언트에서 삭제 처리
        return ResponseEntity.ok(ApiResponse.success("로그아웃 성공", null));
    }
}
