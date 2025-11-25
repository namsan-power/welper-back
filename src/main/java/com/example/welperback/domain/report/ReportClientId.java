package com.example.welperback.domain.report;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ReportClientId implements Serializable {

    @Column(name = "report_id")
    private Long reportId;

    @Column(name = "client_id")
    private Long clientId;

    public ReportClientId() {
    }

    public ReportClientId(Long reportId, Long clientId) {
        this.reportId = reportId;
        this.clientId = clientId;
    }

    public Long getReportId() {
        return reportId;
    }

    public Long getClientId() {
        return clientId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReportClientId that)) {
            return false;
        }
        return Objects.equals(reportId, that.reportId) && Objects.equals(clientId, that.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportId, clientId);
    }
}
