package com.example.welperback.controller.auth;

import com.example.welperback.dto.auth.request.LoginRequest;
import com.example.welperback.dto.auth.request.ResetPasswordRequest;
import com.example.welperback.dto.auth.request.UpdatePasswordRequest;
import com.example.welperback.dto.auth.response.DashboardResponse;
import com.example.welperback.dto.auth.response.LoginResponse;
import com.example.welperback.dto.auth.request.SignupRequest;
import com.example.welperback.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import com.example.welperback.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth API", description = "회원가입, 로그인, 비밀번호 재설정 및 사용자 정보 조회 API")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ✅ 회원가입
    @Operation(
            summary = "회원가입",
            description = "이메일, 비밀번호, 이름, 전화번호를 이용해 회원가입합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200", description = "회원가입 성공"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400", description = "이미 존재하는 이메일"
                    )
            }
    )
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.ok(ApiResponse.success("회원가입 성공", null));
    }

    // ✅ 로그인
    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호를 통해 로그인 후 Access Token과 Refresh Token을 발급받습니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200", description = "로그인 성공",
                            content = @Content(schema = @Schema(implementation = LoginResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401", description = "잘못된 이메일 또는 비밀번호"
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", response));
    }

    // ✅ 로그아웃
    @Operation(
            summary = "로그아웃",
            description = "서버 측 세션이 없다면, 클라이언트에서 토큰을 삭제합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200", description = "로그아웃 성공"
                    )
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(ApiResponse.success("로그아웃 성공", null));
    }

    // ✅ 비밀번호 재설정 요청 (이메일 전송)
    @Operation(
            summary = "비밀번호 재설정 요청",
            description = "가입된 이메일로 비밀번호 재설정 링크를 발송합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200", description = "비밀번호 재설정 이메일 전송 완료"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404", description = "존재하지 않는 이메일"
                    )
            }
    )
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.sendResetLink(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success("비밀번호 재설정 이메일 전송 완료", null));
    }

    // ✅ 비밀번호 실제 변경
    @Operation(
            summary = "비밀번호 변경",
            description = "이메일로 받은 토큰과 새로운 비밀번호를 입력해 비밀번호를 변경합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200", description = "비밀번호 변경 성공"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400", description = "유효하지 않거나 만료된 토큰"
                    )
            }
    )
    @PostMapping("/update-password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(@RequestBody UpdatePasswordRequest request) {
        authService.updatePassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success("비밀번호가 성공적으로 변경되었습니다.", null));
    }

    // ✅ 로그인한 사용자 정보 조회 (JWT 필요)
    @Operation(
            summary = "사용자 정보 조회",
            description = "로그인한 사용자의 이름, 이메일, 최근 로그인 시간 등을 반환합니다.",
            security = @SecurityRequirement(name = "bearerAuth"), // 🔐 Swagger JWT 헤더 표시
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200", description = "사용자 정보 조회 성공",
                            content = @Content(schema = @Schema(implementation = DashboardResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401", description = "인증 실패 (JWT 누락 또는 만료)"
                    )
            }
    )
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<DashboardResponse>> getUserInfo() {
        DashboardResponse response = authService.getDashboardInfo();
        return ResponseEntity.ok(ApiResponse.success("사용자 정보 조회 성공", response));
    }
}
