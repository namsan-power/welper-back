package com.example.welperback.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ReportSummaryDto {
	private Long reportId;
	private String title;
	private LocalDateTime createdAt;
	private Long authorId;
}



