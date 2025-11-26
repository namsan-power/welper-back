package com.example.welperback.domain.execution;

import com.example.welperback.domain.account.User;
import com.example.welperback.domain.assessment.ServicePlan;
import com.example.welperback.domain.client.Client;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "process_record")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessRecord {

    @Id
    private String recordId;

    @ManyToOne
    @JoinColumn(name = "caseNumber")
    private Client client;

    private LocalDate recordDate;

    private String type; // MONTHLY, SESSION

    private String method;

    @ManyToOne
    @JoinColumn(name = "relatedPlanId")
    private ServicePlan relatedPlan;

    private String targetGoalId;

    @Column(columnDefinition = "text")
    private String content;

    @ManyToOne
    @JoinColumn(name = "managerId")
    private User manager;
}
