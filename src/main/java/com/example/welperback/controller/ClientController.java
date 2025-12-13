package com.example.welperback.controller;

import com.example.welperback.dto.client.ClientCreateRequest;
import com.example.welperback.dto.client.ClientDetailResponse;
import com.example.welperback.dto.client.ClientListDto;
import com.example.welperback.dto.client.ClientUpdateRequest;
import com.example.welperback.global.response.ApiResponse;
import com.example.welperback.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@Tag(name = "Client", description = "클라이언트 관리 API")
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    @Operation(
        summary = "클라이언트 목록 조회", 
        description = "로그인한 사용자의 역할에 따라 클라이언트 목록을 조회합니다. SUPERVISOR는 모든 클라이언트, CASE_MANAGER는 자신에게 배정된 클라이언트만 조회합니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<List<ClientListDto>> getClientList() {
        List<ClientListDto> clients = clientService.getClientList();
        return ApiResponse.success(clients);
    }

    @PostMapping
    @Operation(
        summary = "클라이언트 등록", 
        description = "새로운 클라이언트를 등록합니다. 사례번호는 자동으로 생성됩니다 (YYYY-###).",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<ClientDetailResponse> createClient(@Valid @RequestBody ClientCreateRequest request) {
        ClientDetailResponse response = clientService.createClient(request);
        return ApiResponse.success(response);
    }

    @PutMapping("/{caseNumber}")
    @Operation(
        summary = "클라이언트 수정", 
        description = "기존 클라이언트 정보를 수정합니다. null이 아닌 필드만 업데이트됩니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<ClientDetailResponse> updateClient(
            @PathVariable String caseNumber,
            @Valid @RequestBody ClientUpdateRequest request) {
        ClientDetailResponse response = clientService.updateClient(caseNumber, request);
        return ApiResponse.success(response);
    }

    @GetMapping("/{caseNumber}")
    @Operation(
        summary = "클라이언트 상세 조회", 
        description = "특정 클라이언트의 상세 정보를 조회합니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<ClientDetailResponse> getClientDetail(@PathVariable String caseNumber) {
        ClientDetailResponse response = clientService.getClientDetail(caseNumber);
        return ApiResponse.success(response);
    }

    @DeleteMapping("/{caseNumber}")
    @Operation(
        summary = "클라이언트 삭제", 
        description = "클라이언트를 삭제합니다 (Soft Delete). 실제로는 deletedAt 필드가 설정됩니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<String> deleteClient(@PathVariable String caseNumber) {
        clientService.deleteClient(caseNumber);
        return ApiResponse.success("클라이언트가 삭제되었습니다.");
    }
}
