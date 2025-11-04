package com.example.welperback.domain.report;

import com.example.welperback.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "report_participant")
public class ReportParticipant {

    @EmbeddedId
    private ReportParticipantId id = new ReportParticipantId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("reportId")
    @JoinColumn(name = "report_id")
    private Report report;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User participant;

    @Column(name = "role", length = 50)
    private String role = "참석자";

    public ReportParticipant() {
    }

    public ReportParticipant(Report report, User participant, String role) {
        this.report = report;
        this.participant = participant;
        this.role = role;
        this.id = new ReportParticipantId(
                report != null ? report.getId() : null,
                participant != null ? participant.getId() : null
        );
    }

    public ReportParticipantId getId() {
        return id;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public User getParticipant() {
        return participant;
    }

    public void setParticipant(User participant) {
        this.participant = participant;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReportParticipant that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
