package com.example.welperback.controller.plan;

import com.example.welperback.dto.plan.ServicePlanResponse;
import com.example.welperback.dto.plan.ServicePlanSaveRequest;
import com.example.welperback.dto.plan.ServicePlanUpdateRequest;
import com.example.welperback.global.response.ApiResponse;
import com.example.welperback.service.plan.ServicePlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(
        name = "Service Plan",
        description = "서비스 계획서(Service Plan) CRUD 및 AI 결과 저장 API"
)
public class ServicePlanController {

    private final ServicePlanService service;

    /**
     * 계획서 저장 (AI 슈퍼비전 결과 반영)
     */
    @Operation(
            summary = "계획서 저장",
            description = """
            AI 슈퍼비전 결과를 기반으로 최종 확정된 서비스 계획서를 저장합니다.
            
            - AI 서버에서 생성된 슈퍼비전 코멘트를 supervisorFeedback으로 저장
            - planItems는 JSON 구조 그대로 저장됩니다
            """
    )
    @PostMapping("/ai/plans/save")
    public ApiResponse<ServicePlanResponse> save(
            @RequestBody @Valid ServicePlanSaveRequest request
    ) {
        return ApiResponse.success(
                "계획서를 저장했습니다.",
                service.save(request)
        );
    }

    /**
     * 계획서 단건 조회
     */
    @Operation(
            summary = "계획서 상세 조회",
            description = "planId 기준으로 단일 서비스 계획서의 상세 정보를 조회합니다."
    )
    @GetMapping("/plans/{planId}")
    public ApiResponse<ServicePlanResponse> get(
            @PathVariable String planId
    ) {
        return ApiResponse.success(service.get(planId));
    }

    /**
     * 사례별 계획서 목록 조회
     */
    @Operation(
            summary = "사례별 계획서 목록 조회",
            description = """
            caseNumber 기준으로 해당 사례에 속한 모든 서비스 계획서를 조회합니다.
            
            - 최신 계획서가 상단에 오도록 정렬됩니다.
            """
    )
    @GetMapping("/plans/case/{caseNumber}")
    public ApiResponse<?> list(
            @PathVariable String caseNumber
    ) {
        return ApiResponse.success(service.getByCase(caseNumber));
    }

    /**
     * 계획서 전체 수정
     */
    @Operation(
            summary = "계획서 수정",
            description = """
            planId 기준으로 서비스 계획서를 전체 수정합니다.
            
            - PUT 방식
            - 전달된 값으로 기존 데이터를 덮어씁니다
            - null 값 전달 시 해당 필드는 null로 저장됩니다
            """
    )
    @PutMapping("/plans/{planId}")
    public ApiResponse<ServicePlanResponse> update(
            @PathVariable String planId,
            @RequestBody @Valid ServicePlanUpdateRequest request
    ) {
        return ApiResponse.success(
                "계획서를 수정했습니다.",
                service.update(planId, request)
        );
    }

    /**
     * 계획서 삭제
     */
    @Operation(
            summary = "계획서 삭제",
            description = """
            planId 기준으로 서비스 계획서를 삭제합니다.
            
            - 현재는 하드 삭제 방식
            - 추후 이력 관리가 필요하면 soft delete로 전환 가능
            """
    )
    @DeleteMapping("/plans/{planId}")
    public ApiResponse<Void> delete(
            @PathVariable String planId
    ) {
        service.delete(planId);
        return ApiResponse.success("계획서를 삭제하였습니다.",null);
    }
}
