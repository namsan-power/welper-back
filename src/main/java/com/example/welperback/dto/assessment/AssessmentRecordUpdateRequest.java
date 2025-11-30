package com.example.welperback.dto.assessment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;

/**
 * 1차 사정 보고서 수정용 DTO.
 * - 부분 수정(PATCH)을 위해 모든 필드를 nullable로 두고,
 *   null이 아닌 것만 반영하는 방식으로 처리.
 */
@Getter
@Setter
public class AssessmentRecordUpdateRequest {

    private LocalDate assessmentDate;
    private String type;

    private String genogramFilePath;
    private String ecomapFilePath;
    private String voiceRecordFilePath;

    private Map<String, Object> checklistData;

    private String strengthsAndResources;
    private String comprehensiveOpinion;
}
