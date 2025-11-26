package com.example.welperback.domain.assessment;

import com.example.welperback.domain.client.Client;
import com.example.welperback.global.jpa.JsonMapConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "assessment_record")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentRecord {

    @Id
    private String recordId;

    @ManyToOne
    @JoinColumn(name = "caseNumber")
    private Client client;

    private LocalDate assessmentDate;

    private String type; // NEW, REASSESSMENT

    private String genogramFilePath;
    private String ecomapFilePath;
    private String voiceRecordFilePath;

    @Convert(converter = JsonMapConverter.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> checklistData;

    private String produceStatus; // PRODUCING, COMPLETE

    @Column(columnDefinition = "text")
    private String strengthsAndResources;

    @Column(columnDefinition = "text")
    private String comprehensiveOpinion;
}
