package com.example.welperback.controller.ai;

import com.example.welperback.dto.ai.AiAssessmentSaveRequest;
import com.example.welperback.dto.ai.AiAssessmentStatusResponse;
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
     * 2번: AI 분석 요청
     */
    @PostMapping("/request")
    public ApiResponse<?> requestAiAssessment(
            @RequestBody AssessmentAiRequestDto requestDto
    ) {

        var result = aiService.startAiAssessment(requestDto);

        Map<String, Object> data = new HashMap<>();
        data.put("requestId", result.requestId());
        data.put("status", result.status());
        data.put("message", result.message());

        return ApiResponse.success(
                "AI 분석 요청을 정상적으로 처리했습니다.",
                data
        );
    }

    /**
     * 3번: AI 분석 결과 조회 (Polling)
     */
    @GetMapping("/result/{requestId}")
    public ApiResponse<?> getAiAssessmentResult(
            @PathVariable String requestId
    ) {

        AiAssessmentStatusResponse statusResponse = aiService.getAssessmentStatus(requestId);

        // NOT_FOUND 처리
        if ("NOT_FOUND".equals(statusResponse.getStatus())) {
            return ApiResponse.error(
                    statusResponse.getMessage(),
                    404,
                    null
            );
        }
        Map<String, Object> data = new HashMap<>();
        data.put("status", statusResponse.getStatus());
        data.put("message", statusResponse.getMessage());
        data.put("assessment", statusResponse.getAssessment()); // PROCESSING이면 null

        return ApiResponse.success(
                "AI 분석 결과 조회를 성공했습니다.",
                data
        );

    }

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
