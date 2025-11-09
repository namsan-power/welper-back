package com.example.welperback.service.auth;

import com.example.welperback.domain.user.PasswordResetToken;
import com.example.welperback.domain.user.User;
import com.example.welperback.dto.auth.request.LoginRequest;
import com.example.welperback.dto.auth.response.DashboardResponse;
import com.example.welperback.dto.auth.response.LoginResponse;
import com.example.welperback.dto.auth.request.SignupRequest;
import com.example.welperback.global.exception.CustomException;
import com.example.welperback.global.exception.ErrorCode;
import com.example.welperback.repository.auth.PasswordResetTokenRepository;
import com.example.welperback.repository.auth.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.welperback.global.security.JwtTokenProvider;

import java.util.UUID;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final MailService mailService;



    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, PasswordResetTokenRepository passwordResetTokenRepository, MailService mailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.mailService = mailService;
    }

    public void signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getName(),
                request.getPhoneNumber(),
                null
        );

        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        return new LoginResponse(accessToken, refreshToken);
    }
    public void sendResetLink(String email) {
        // 1. 이메일 확인
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 검사 : 기존 토큰이 있으면 먼저 삭제
        passwordResetTokenRepository.findByUser(user)
                .ifPresent(token -> {
                    passwordResetTokenRepository.delete(token);
                    passwordResetTokenRepository.flush(); // ✅ 즉시 DB에서 삭제,트랜잭션 안에서 delete와 insert가 같은 flush cycle에서 동시에 처리되기 때문에 duplicate 에러 발생
                });

        // 2 토큰 생성
        String token = UUID.randomUUID().toString();

        // 3 토큰 저장
        passwordResetTokenRepository.save(new PasswordResetToken(user, token));

        // 4 이메일 전송
        String resetLink = "https://welper.store/reset-password?token=" + token;
        mailService.sendResetEmail(user.getEmail(), resetLink);
    }
    public void updatePassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

        if (resetToken.isExpired()) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        User user = resetToken.getUser();
        user.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // 사용한 토큰 삭제
        passwordResetTokenRepository.delete(resetToken);
    }
    public DashboardResponse getDashboardInfo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return new DashboardResponse(user.getName(), user.getEmail());
    }


}
