package com.example.welperback.service;

import com.example.welperback.domain.account.RefreshToken;
import com.example.welperback.domain.account.User;
import com.example.welperback.dto.auth.*;
import com.example.welperback.global.exception.CustomException;
import com.example.welperback.global.exception.ErrorCode;
import com.example.welperback.global.security.JwtTokenProvider;
import com.example.welperback.repository.account.RefreshTokenRepository;
import com.example.welperback.repository.account.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final com.example.welperback.repository.account.AdminRepository adminRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 사용자 로그인 (User 또는 Admin)
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        String userId = request.getUserId();
        String password = request.getPassword();

        // User 또는 Admin 조회
        User user = userRepository.findByUserId(userId).orElse(null);
        
        if (user != null) {
            // User 로그인
            return loginUser(user, password);
        }

        // Admin 조회
        com.example.welperback.domain.account.Admin admin = 
            adminRepository.findByAdminId(userId).orElse(null);

        if (admin != null) {
            // Admin 로그인
            return loginAdmin(admin, password);
        }

        // 사용자를 찾을 수 없음
        throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }

    /**
     * User 로그인 처리
     */
    private LoginResponse loginUser(User user, String password) {
        // 비밀번호 검증
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        // INACTIVE 상태 체크
        if ("INACTIVE".equals(user.getStatus())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        // Access Token 생성 (Role 포함)
        String accessToken = jwtTokenProvider.createAccessToken(user.getUserId(), user.getRole());

        // Refresh Token 생성 및 저장
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserId());
        saveRefreshToken(user.getUserId(), refreshToken);

        // UserDto 생성
        UserDto userDto = UserDto.from(user);

        log.info("User logged in successfully: {}", user.getUserId());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userDto)
                .build();
    }

    /**
     * Admin 로그인 처리
     */
    private LoginResponse loginAdmin(com.example.welperback.domain.account.Admin admin, String password) {
        // 비밀번호 검증
        if (!passwordEncoder.matches(password, admin.getPasswordHash())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        // Access Token 생성 (Role 포함)
        String accessToken = jwtTokenProvider.createAccessToken(admin.getAdminId(), admin.getRole());

        // Refresh Token 생성 및 저장
        String refreshToken = jwtTokenProvider.createRefreshToken(admin.getAdminId());
        saveRefreshToken(admin.getAdminId(), refreshToken);

        // UserDto 생성 (Admin을 UserDto로 변환)
        UserDto userDto = UserDto.builder()
                .userId(admin.getAdminId())
                .name(admin.getName())
                .role(admin.getRole())
                .agencyName(admin.getAgencyName())
                .status("ACTIVE")
                .build();

        log.info("Admin logged in successfully: {}", admin.getAdminId());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userDto)
                .build();
    }

    /**
     * 토큰 갱신
     */
    @Transactional
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Refresh Token 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // Token Type 검증
        String tokenType = jwtTokenProvider.getTokenType(refreshToken);
        if (!"REFRESH".equals(tokenType)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // DB에서 Refresh Token 조회
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

        // 만료 확인
        if (storedToken.isExpired()) {
            refreshTokenRepository.delete(storedToken);
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        }

        // 사용자 조회
        User user = userRepository.findByUserId(storedToken.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 새로운 Access Token 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(user.getUserId(), user.getRole());

        // 새로운 Refresh Token 발급
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getUserId());
        
        // 기존 Refresh Token 삭제 및 새로운 Token 저장
        refreshTokenRepository.delete(storedToken);
        saveRefreshToken(user.getUserId(), newRefreshToken);

        UserDto userDto = UserDto.from(user);

        log.info("Token refreshed successfully for user: {}", user.getUserId());

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .user(userDto)
                .build();
    }

    /**
     * 로그아웃
     */
    @Transactional
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        String userId = authentication.getName();
        
        // Refresh Token 삭제
        refreshTokenRepository.deleteByUserId(userId);
        
        log.info("User logged out successfully: {}", userId);
    }

    /**
     * 사용자 등록
     */
    @Transactional
    public UserDto register(RegisterRequest request) {
        // 중복 확인
        if (userRepository.existsByUserId(request.getUserId())) {
            throw new CustomException(ErrorCode.DUPLICATE_USER_ID);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 사용자 생성
        User user = User.builder()
                .userId(request.getUserId())
                .passwordHash(encodedPassword)
                .name(request.getName())
                .role(request.getRole())
                .agencyName(request.getAgencyName())
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        log.info("New user registered: {}", savedUser.getUserId());

        return UserDto.from(savedUser);
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        String userId = authentication.getName();

        // 사용자 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        // 새 비밀번호가 현재 비밀번호와 같은지 확인
        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        // 비밀번호 변경
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPasswordHash(encodedPassword);

        // 모든 Refresh Token 삭제 (재로그인 필요)
        refreshTokenRepository.deleteByUserId(userId);

        log.info("Password changed successfully for user: {}", userId);
    }

    /**
     * Refresh Token 저장 (Upsert 방식)
     * 기존 토큰이 있으면 업데이트, 없으면 새로 생성
     */
    private void saveRefreshToken(String userId, String token) {
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);
        
        // 기존 Refresh Token 조회
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUserId(userId);
        
        if (existingToken.isPresent()) {
            // 기존 토큰이 있으면 업데이트
            RefreshToken refreshToken = existingToken.get();
            refreshToken.setToken(token);
            refreshToken.setExpiresAt(expiresAt);
            refreshTokenRepository.save(refreshToken);
        } else {
            // 기존 토큰이 없으면 새로 생성
            RefreshToken refreshToken = RefreshToken.builder()
                    .userId(userId)
                    .token(token)
                    .expiresAt(expiresAt)
                    .build();
            refreshTokenRepository.save(refreshToken);
        }
    }

    /**
     * 현재 로그인한 사용자 정보 조회
     */
    public UserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        String userId = authentication.getName();
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return UserDto.from(user);
    }
}
