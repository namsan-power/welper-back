package com.example.welperback.domain.execution;

import com.example.welperback.domain.account.User;
import com.example.welperback.domain.client.Client;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "supervision_record")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupervisionRecord {

    @Id
    private String recordId;

    @ManyToOne
    @JoinColumn(name = "caseNumber")
    private Client client;

    private LocalDate supervisionDate;

    @ManyToOne
    @JoinColumn(name = "supervisorId")
    private User supervisor;

    @Column(columnDefinition = "text")
    private String topic;

    @Column(columnDefinition = "text")
    private String content;

    @Column(columnDefinition = "text")
    private String outcome;
}
