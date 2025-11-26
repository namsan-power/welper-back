package com.example.welperback.repository.account;

import com.example.welperback.domain.account.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserId(String userId);
    void deleteByUserId(String userId);
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}

