package com.example.welperback.controller.ai;

import com.example.welperback.dto.ai.AiServicePlanStatusResponse;
import com.example.welperback.dto.ai.ServicePlanAiRequestDto;
import com.example.welperback.global.response.ApiResponse;
import com.example.welperback.service.ai.ServicePlanAiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai/plans")
@RequiredArgsConstructor
@Tag(name = "ServicePlan-AI", description = "계획서 AI 파트(초안 생성/폴링)")
public class ServicePlanAiController {

    private final ServicePlanAiService planAiService;

    @Operation(
            summary = "계획서 초안 생성 요청",
            description = "caseNumber 기준으로 DB의 1차 사정결과(AssessmentRecord)를 조회해 AI 서버로 전달하고, 작업을 큐에 등록한다."
    )
    @PostMapping({"/access", "/acess"}) // ✅ 스크린샷 typo 대응(둘 다 열어둠)
    public ApiResponse<AiServicePlanStatusResponse> requestPlanDraft(
            @RequestBody ServicePlanAiRequestDto requestDto
    ) {
        AiServicePlanStatusResponse response = planAiService.requestPlanDraft(requestDto);
        return ApiResponse.success("AI 계획서 초안 생성 작업을 등록했습니다.", response);
    }

    @Operation(
            summary = "AI 계획서 초안 생성 상태 조회(Polling)",
            description = "caseNumber 기준으로 계획서 초안 생성 상태/결과를 조회한다."
    )
    @GetMapping("/{caseNumber}")
    public ApiResponse<AiServicePlanStatusResponse> getPlanDraftStatus(
            @PathVariable String caseNumber
    ) {
        AiServicePlanStatusResponse response = planAiService.getCaseStatus(caseNumber);
        return ApiResponse.success("AI 계획서 상태 조회를 성공했습니다.", response);
    }
}
