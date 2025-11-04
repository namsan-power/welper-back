package com.example.welperback.domain.report;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ReportParticipantId implements Serializable {

    @Column(name = "report_id")
    private Long reportId;

    @Column(name = "user_id")
    private Long userId;

    public ReportParticipantId() {
    }

    public ReportParticipantId(Long reportId, Long userId) {
        this.reportId = reportId;
        this.userId = userId;
    }

    public Long getReportId() {
        return reportId;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReportParticipantId that)) {
            return false;
        }
        return Objects.equals(reportId, that.reportId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportId, userId);
    }
}
