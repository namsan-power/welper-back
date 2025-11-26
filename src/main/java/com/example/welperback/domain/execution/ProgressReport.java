package com.example.welperback.domain.execution;

import com.example.welperback.domain.client.Client;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "progress_report")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressReport {

    @Id
    private String reportId;

    @ManyToOne
    @JoinColumn(name = "caseNumber")
    private Client client;

    private LocalDate analysisStartDate;
    private LocalDate analysisEndDate;

    @Column(columnDefinition = "text")
    private String summaryContent;

    @Column(columnDefinition = "text")
    private String achievementAnalysis;
}
