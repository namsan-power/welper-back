package com.example.welperback.domain.result;

import com.example.welperback.domain.client.Client;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "followup_report")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowUpReport {

    @Id
    private String reportId;

    @ManyToOne
    @JoinColumn(name = "caseNumber")
    private Client client;

    private LocalDate checkDate;

    @Column(columnDefinition = "text")
    private String content;

    @Column(columnDefinition = "text")
    private String outcome;

    private Boolean isReinterventionNeeded;
}
