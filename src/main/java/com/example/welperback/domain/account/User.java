package com.example.welperback.domain.account;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private String userId;

    private String passwordHash;

    private String name;

    private String role; // SUPERVISOR, CASE_MANAGER

    private String agencyName;

    private String status; // ACTIVE, INACTIVE

    private LocalDateTime createdAt;

    // FK: createdByAdminId
    @ManyToOne
    @JoinColumn(name = "createdByAdminId")
    private Admin createdBy;
}
