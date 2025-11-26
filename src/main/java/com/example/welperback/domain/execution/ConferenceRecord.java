package com.example.welperback.domain.execution;

import com.example.welperback.domain.client.Client;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "conference_record")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConferenceRecord {

    @Id
    private String recordId;

    @ManyToOne
    @JoinColumn(name = "caseNumber")
    private Client client;

    private LocalDate meetingDate;

    @Column(columnDefinition = "text")
    private String attendees;

    @Column(columnDefinition = "text")
    private String topic;

    @Column(columnDefinition = "text")
    private String decision;
}
