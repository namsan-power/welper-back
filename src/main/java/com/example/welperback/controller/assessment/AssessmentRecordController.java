package com.example.welperback.controller.assessment;

import com.example.welperback.dto.assessment.AssessmentRecordResponse;
import com.example.welperback.dto.assessment.AssessmentRecordUpdateRequest;
import com.example.welperback.global.response.ApiResponse;
import com.example.welperback.service.assessment.AssessmentRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assessments")
@RequiredArgsConstructor
public class AssessmentRecordController {

    private final AssessmentRecordService assessmentRecordService;

    /**
     * 1) 단일 1차 사정 보고서 조회 (recordId 기준)
     *
     * GET /api/v1/assessments/{recordId}
     */
    @GetMapping("/{recordId}")
    public ApiResponse<AssessmentRecordResponse> getAssessmentByRecordId(
            @PathVariable String recordId
    ) {
        AssessmentRecordResponse response = assessmentRecordService.getByRecordId(recordId);
        return ApiResponse.success(
                "사정 보고서 조회를 성공했습니다.",
                response
        );
    }

    /**
     * 2) 특정 사례의 모든 사정 보고서 조회 (caseNumber 기준)
     *
     * GET /api/v1/assessments/case/{caseNumber}
     *
     * - 1차/재사정 모두 포함.
     * - FE에서 type == "NEW" 만 필터링하면 “1차 사정만”도 쉽게 구현 가능.
     */
    @GetMapping("/case/{caseNumber}")
    public ApiResponse<List<AssessmentRecordResponse>> getAssessmentsByCaseNumber(
            @PathVariable String caseNumber
    ) {
        List<AssessmentRecordResponse> list = assessmentRecordService.getByCaseNumber(caseNumber);
        return ApiResponse.success(
                "사레별 사정 보고서 목록 조회를 성공했습니다.",
                list
        );
    }

    /**
     * 보고서 전체 수정
     *
     * PUT /api/v1/assessments/{recordId}
     *
     * - 수정 화면에서 만들어진 보고서 전체 JSON을 통째로 보낸다.
     */
    @PutMapping("/{recordId}")
    public ApiResponse<AssessmentRecordResponse> updateAssessmentFully(
            @PathVariable String recordId,
            @RequestBody AssessmentRecordUpdateRequest request
    ) {
        AssessmentRecordResponse updated = assessmentRecordService.updateFully(recordId, request);
        return ApiResponse.success(
                "사정 보고서를 수정했습니다.",
                updated
        );
    }

    /**
     * 4) 사정 보고서 삭제
     *
     * DELETE /api/v1/assessments/{recordId}
     */
    @DeleteMapping("/{recordId}")
    public ApiResponse<?> deleteAssessment(
            @PathVariable String recordId
    ) {
        assessmentRecordService.delete(recordId);
        return ApiResponse.success(
                "사정 보고서를 삭제했습니다.",
                null
        );
    }
}
