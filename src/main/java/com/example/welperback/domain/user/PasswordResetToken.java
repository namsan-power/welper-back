package com.example.welperback.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne
    private User user;

    private LocalDateTime createdAt;

    public PasswordResetToken(User user, String token) {
        this.user = user;
        this.token = token;
        this.createdAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return createdAt.plusMinutes(30).isBefore(LocalDateTime.now());
    }
}