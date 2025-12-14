package com.example.welperback.domain.client;

import com.example.welperback.domain.account.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "client")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @Column(name = "case_number")
    private String caseNumber;


    private String clientName;

    @ManyToOne
    @JoinColumn(name = "assignedManagerId")
    private User assignedManager;

    private String caseStatus; // RECEPTION, SELECTED, NOT_SELECTED, TERMINATED

    private LocalDate registrationDate;
    private LocalDate birthDate;
    private String gender;
    private String contactNumber;
    private String address;

    private Boolean privacyConsent;

    private String consentFilePath;

    private LocalDateTime deletedAt;
}
