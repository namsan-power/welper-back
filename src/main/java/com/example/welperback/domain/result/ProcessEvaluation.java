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
@Table(name = "process_evaluation")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessEvaluation {

    @Id
    private String recordId;

    @ManyToOne
    @JoinColumn(name = "caseNumber")
    private Client client;

    private LocalDate evaluationDate;

    @Column(columnDefinition = "text")
    private String content;

    private String filePath;

    @Convert(converter = JsonMapConverter.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> implementationReview;

    private String result; // MAINTAIN, TERMINATE, REASSESS
}
