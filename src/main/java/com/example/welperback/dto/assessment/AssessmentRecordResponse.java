package com.example.welperback.dto.assessment;

import com.example.welperback.domain.assessment.AssessmentRecord;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Builder
public class AssessmentRecordResponse {

    private String recordId;
    private String caseNumber;

    private LocalDate assessmentDate;
    private String type;

    private String genogramFilePath;
    private String ecomapFilePath;
    private String voiceRecordFilePath;

    private Map<String, Object> checklistData;

    private String strengthsAndResources;
    private String comprehensiveOpinion;

    public static AssessmentRecordResponse fromEntity(AssessmentRecord record) {
        return AssessmentRecordResponse.builder()
                .recordId(record.getRecordId())
                .caseNumber(record.getClient().getCaseNumber())
                .assessmentDate(record.getAssessmentDate())
                .type(record.getType())
                .genogramFilePath(record.getGenogramFilePath())
                .ecomapFilePath(record.getEcomapFilePath())
                .voiceRecordFilePath(record.getVoiceRecordFilePath())
                .checklistData(record.getChecklistData())
                .strengthsAndResources(record.getStrengthsAndResources())
                .comprehensiveOpinion(record.getComprehensiveOpinion())
                .build();
    }
}
