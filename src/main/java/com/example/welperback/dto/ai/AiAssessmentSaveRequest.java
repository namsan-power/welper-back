package com.example.welperback.dto.ai;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiAssessmentSaveRequest {

    // === 어떤 클라이언트(사례)에 대한 사정인지 ===
    private String caseNumber;           // ★ 이걸로 Client 찾을 거야

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate assessmentDate;    // 2025-01-20

    private String type;                 // NEW, REASSESSMENT
    private String managerId;            // 담당자 기록용(지금은 AssessmentRecord에 직접 FK 없으니까 로그 느낌)

    // === 파일 / 음성 경로 (지금은 단순 String) ===
    private String genogramFileId;       // -> genogramFilePath
    private String ecomapFileId;         // -> ecomapFilePath
    private String voiceRecordFileUrl;   // -> voiceRecordFilePath

    /**
     * checklistData 전체:
     *  - meetingLogs
     *  - clientProfile
     *  - emergencyContact
     *  - householdType
     *  - disabilityStatus
     *  - longTermCareStatus
     *  - housingStatus
     *  - familyMembers
     *  - needsAssessmentRecord
     *  - scaleAssessments
     *  - assessmentOutcome
     *
     * 그대로 AssessmentRecord.checklistData(jsonb)에 넣을 거야.
     */
    private Map<String, Object> checklistData;

    // === AI가 생성한 텍스트 요약 ===
    private String strengthsAndResources;   // 강점·자원
    private String comprehensiveOpinion;    // 종합의견
}
