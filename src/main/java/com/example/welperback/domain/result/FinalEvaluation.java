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
@Table(name = "final_evaluation")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinalEvaluation {

    @Id
    private String recordId;

    @OneToOne
    @JoinColumn(name = "caseNumber")
    private Client client;

    private LocalDate evaluationDate;

    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonMapConverter.class)
    private Map<String, Object> goalAchievement;

    private String finalResult;
}
