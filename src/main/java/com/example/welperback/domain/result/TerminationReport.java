package com.example.welperback.domain.result;

import com.example.welperback.domain.client.Client;
import com.example.welperback.global.jpa.JsonMapConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "termination_report")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TerminationReport {

    @Id
    private String reportId;

    @OneToOne
    @JoinColumn(name = "caseNumber")
    private Client client;

    private LocalDate terminationDate;

    @Column(columnDefinition = "text")
    private String contentSummary;

    private String filePath;

    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonMapConverter.class)
    private Map<String, Object> reasons;

    @Column(columnDefinition = "text")
    private String followUpPlan;
}
