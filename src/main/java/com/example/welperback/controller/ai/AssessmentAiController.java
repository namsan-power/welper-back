package com.example.welperback.controller.ai;

import com.example.welperback.dto.ai.AiAssessmentSaveRequest;
import com.example.welperback.dto.ai.AiAssessmentStatusResponse;
import com.example.welperback.dto.ai.AiJobResponse;
import com.example.welperback.dto.ai.AssessmentAiRequestDto;
import com.example.welperback.global.response.ApiResponse;
import com.example.welperback.service.ai.AssessmentAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai/assessments")
@RequiredArgsConstructor
public class AssessmentAiController {

    private final AssessmentAiService aiService;
    /**
     * 2번: 특정 사례(caseNumber)에 대한 AI 사정 분석 Job 등록
     *
     * - Body에 caseNumber가 포함된 AssessmentAiRequestDto를 받음
     * - 응답에는 caseNumber와 status만 내려줌 (jobId는 노출하지 않음)
     */
    @PostMapping("/request")
    public ApiResponse<AiAssessmentStatusResponse> requestAiAssessment(
            @RequestBody AssessmentAiRequestDto requestDto
    ) {
      AiAssessmentStatusResponse response = aiService.requestAiAssessment(requestDto);
//        AiAssessmentStatusResponse response = aiService.requestAiAndGetResult(requestDto);


        return ApiResponse.success(
                "AI 사정 분석 작업을 등록했습니다.",
                response
        );
    }

    /**
     * 3번: 사례 번호(caseNumber) 기준으로 AI 사정 상태 & 결과 조회
     *
     * - 프론트에서는 caseNumber만 알고 있으면 됨.
     * - status:
     *   - NONE       : AI 분석 요청 자체가 없을 때
     *   - PROCESSING : 분석 중
     *   - FINISHED   : 완료 (assessment 필드에 JSON 포함)
     *   - FAILED     : 실패 (message에 오류 내용 포함)
     */
    @GetMapping("/{caseNumber}")
    public ApiResponse<AiAssessmentStatusResponse> getAiAssessmentByCase(
            @PathVariable String caseNumber
    ) {
        AiAssessmentStatusResponse statusResponse = aiService.getCaseStatus(caseNumber);

        return ApiResponse.success(
                "AI 사정 분석 상태 조회를 성공했습니다.",
                statusResponse
       );
    }
    // =========================================================================
    // =========================================================================

    /**
     * 2번: AI 1차 사정 분석 요청
     *  - 이제 여기서 바로 AI 서버까지 갔다 와서 결과를 돌려줌
     */
//    @PostMapping("/request")
//    public ApiResponse<?> requestAiAssessment(
//            @RequestBody AssessmentAiRequestDto requestDto
//    ) {
//        // AI 서버까지 요청/응답을 처리한 최종 결과
//        AiAssessmentStatusResponse response;
//        response = aiService.requestAiAndGetResult(requestDto);
//
//        return ApiResponse.success(
//                "AI 분석을 정상적으로 완료했습니다.",
//                response
//        );
//    }

    // 4번: AI 사정 결과 DB 저장
    @PostMapping("/result/save")
    public ApiResponse<?> saveAiAssessment(
            @RequestBody AiAssessmentSaveRequest request
    ) {

        // 간단 필수값 체크 (정제는 나중에 Bean Validation 써도 됨)
        if (request.getCaseNumber() == null || request.getCaseNumber().isBlank()) {
            return ApiResponse.error("필수 필드 누락: caseNumber", 400, null);
        }
        if (request.getAssessmentDate() == null) {
            return ApiResponse.error("필수 필드 누락: assessmentDate", 400, null);
        }
        if (request.getType() == null || request.getType().isBlank()) {
            return ApiResponse.error("필수 필드 누락: type", 400, null);
        }

        String recordId = aiService.saveAiAssessment(request);

        Map<String, Object> data = new HashMap<>();
        data.put("assessmentRecordId", recordId);

        return ApiResponse.success(
                "AI 사정 결과를 저장했습니다.",
                data
        );
    }
}
