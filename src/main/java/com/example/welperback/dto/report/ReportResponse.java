package com.example.welperback.dto.report;

import com.example.welperback.domain.report.Report;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class ReportResponse {
    private Long reportId;
    private String title;
    private LocalDateTime createdAt;
    private String voiceRecord;
    private String aiAnalysisData;

    public static ReportResponse from(Report report) {
        return ReportResponse.builder()
                .reportId(report.getId())
                .title(report.getTitle())
                .createdAt(report.getCreatedAt())
                .voiceRecord(report.getVoiceRecord())
                .aiAnalysisData(report.getAiAnalysisData())
                .build();
    }
}
