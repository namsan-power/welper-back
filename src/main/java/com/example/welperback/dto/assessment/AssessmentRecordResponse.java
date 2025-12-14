package com.example.welperback.dto.assessment;

import com.example.welperback.domain.assessment.AssessmentRecord;
import com.example.welperback.dto.file.DocumentResponse;
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

    // ✅ String filePath 대신 DocumentResponse
    private DocumentResponse genogramFile;
    private DocumentResponse ecomapFile;
    private DocumentResponse voiceRecordFile;

    private Map<String, Object> checklistData;

    private String strengthsAndResources;
    private String comprehensiveOpinion;

    public static AssessmentRecordResponse fromEntity(AssessmentRecord record) {
        return AssessmentRecordResponse.builder()
                .recordId(record.getRecordId())
                .caseNumber(record.getClient().getCaseNumber())
                .assessmentDate(record.getAssessmentDate())
                .type(record.getType())
                .genogramFile(record.getGenogramFile() != null ? DocumentResponse.from(record.getGenogramFile()) : null)
                .ecomapFile(record.getEcomapFile() != null ? DocumentResponse.from(record.getEcomapFile()) : null)
                .voiceRecordFile(record.getVoiceRecordFile() != null ? DocumentResponse.from(record.getVoiceRecordFile()) : null)
                .checklistData(record.getChecklistData())
                .strengthsAndResources(record.getStrengthsAndResources())
                .comprehensiveOpinion(record.getComprehensiveOpinion())
                .build();
    }
}
