package com.example.welperback.domain.account;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "admin")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {

    @Id
    private String adminId;

    private String passwordHash;

    private String name;

    private String role; // SUPER_ADMIN, AGENCY_ADMIN

    private String agencyName;

    private LocalDateTime createdAt;
}
