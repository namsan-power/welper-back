package com.example.welperback.repository.account;

import com.example.welperback.domain.account.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserId(String userId);
    
    @Modifying
    @Transactional
    void deleteByUserId(String userId);
    
    @Modifying
    @Transactional
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}

