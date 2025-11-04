package com.example.welperback.dto.report.request;

import lombok.Getter;
import java.util.List;

@Getter
public class ReportCreateRequestDto {
    private String title;
    private List<Long> clientIds;
    private List<Long> participantIds;

}
