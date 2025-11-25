package com.example.welperback.domain.report;

import com.example.welperback.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@IdClass(ReportParticipant.Pk.class)
@Table(name = "report_participant")
public class ReportParticipant {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
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
        return Objects.equals(getReportId(), that.getReportId())
                && Objects.equals(getParticipantId(), that.getParticipantId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getReportId(), getParticipantId());
    }

    private Long getReportId() {
        return report != null ? report.getId() : null;
    }

    private Long getParticipantId() {
        return participant != null ? participant.getId() : null;
    }

    public static class Pk implements java.io.Serializable {

        private Long report;

        private Long participant;

        public Pk() {
        }

        public Pk(Long report, Long participant) {
            this.report = report;
            this.participant = participant;
        }

        public Long getReport() {
            return report;
        }

        public Long getParticipant() {
            return participant;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Pk pk)) {
                return false;
            }
            return Objects.equals(report, pk.report) && Objects.equals(participant, pk.participant);
        }

        @Override
        public int hashCode() {
            return Objects.hash(report, participant);
        }
    }
}
