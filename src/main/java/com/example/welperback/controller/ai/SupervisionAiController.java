package com.example.welperback.controller.ai;

import com.example.welperback.dto.ai.supervision.AiSupervisionSearchRequest;
import com.example.welperback.dto.ai.supervision.AiSupervisionSearchResponse;
import com.example.welperback.global.response.ApiResponse;
import com.example.welperback.service.ai.SupervisionAiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ai/supervision")
@RequiredArgsConstructor
@Tag(name = "AI Supervision", description = "AI 유사 사례 기반 ServicePlan 추천")
public class SupervisionAiController {

    private final SupervisionAiService supervisionAiService;

    @PostMapping("/search")
    @Operation(
            summary = "AI 유사 사례 검색",
            description = "자연어 질의를 AI 서버로 보내 유사 사례를 찾고, 해당 case의 최신 ServicePlan을 반환합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<List<AiSupervisionSearchResponse>> search(
            @Valid @RequestBody AiSupervisionSearchRequest request
    ) {
        List<AiSupervisionSearchResponse> responses = supervisionAiService.search(request);
        return ApiResponse.success(responses);
    }
}

