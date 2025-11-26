package com.example.welperback.domain.client;

import com.example.welperback.global.jpa.JsonMapConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "intake_record")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntakeRecord {

    @Id
    private String recordId;

    @OneToOne
    @JoinColumn(name = "caseNumber")
    private Client client;

    private String receptionistId;
    private String referralSource;

    @Column(columnDefinition = "text")
    private String initialNeedsSummary;

    private LocalDateTime interviewDate;

    private String interviewType;

    // JSONB
    @Convert(converter = JsonMapConverter.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> disabilityStatus;

    @Convert(converter = JsonMapConverter.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> familyMembers;

    @Convert(converter = JsonMapConverter.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> needsCategories;

    private String intakeResult; // SELECTED / NOT_SELECTED
}
