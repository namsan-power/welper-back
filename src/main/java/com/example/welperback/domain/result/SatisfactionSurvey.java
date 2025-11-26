package com.example.welperback.domain.result;

import com.example.welperback.domain.client.Client;
import com.example.welperback.global.jpa.JsonMapConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "satisfaction_survey")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SatisfactionSurvey {

    @Id
    private String surveyId;

    @OneToOne
    @JoinColumn(name = "caseNumber")
    private Client client;

    private String scanFilePath;

    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonMapConverter.class)
    private Map<String, Object> scores;
}
