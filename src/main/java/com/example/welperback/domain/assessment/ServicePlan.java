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
@Table(name = "service_plan")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicePlan {

    @Id
    private String planId;

    @ManyToOne
    @JoinColumn(name = "caseNumber")
    private Client client;

    private LocalDate planDate;

    // JSONB 배열
    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonMapConverter.class)
    private Map<String, Object> planItems;

    private String contractFilePath;

    @Column(columnDefinition = "text")
    private String supervisorFeedback;
}
