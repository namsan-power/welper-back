package com.example.welperback.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class ReportCreateRequest {
    private String title;
    private List<Long> participantIds;
    private List<Long> clientIds;
    private String voiceFileUrl;
}
