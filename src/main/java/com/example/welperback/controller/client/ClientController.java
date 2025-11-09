package com.example.welperback.controller.client;

import com.example.welperback.dto.client.request.ClientCreateRequest;
import com.example.welperback.dto.client.request.ClientUpdateRequest;
import com.example.welperback.dto.client.response.ClientDetailResponse;
import com.example.welperback.dto.client.response.ClientListResponse;
import com.example.welperback.global.response.ApiResponse;
import com.example.welperback.service.client.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Client API", description = "클라이언트 관리 관련 API")
@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    // ✅ 1. 특정 유저의 클라이언트 리스트 조회
    @Operation(
            summary = "클라이언트 리스트 조회",
            description = "특정 유저(userId)가 담당한 모든 클라이언트 정보를 조회합니다.",
            parameters = {
                    @Parameter(name = "userId", description = "유저 ID", example = "1")
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = ClientListResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 유저 또는 클라이언트 없음")
            }
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ClientListResponse>>> getClientsByUser(
            @PathVariable Long userId) {
        List<ClientListResponse> clients = clientService.getClientsByUser(userId);
        return ResponseEntity.ok(ApiResponse.success("클라이언트 리스트 조회 성공", clients));
    }

    // ✅ 2. 클라이언트 상세 조회
    @Operation(
            summary = "클라이언트 상세 조회",
            description = "클라이언트 ID를 기반으로 상세 정보를 조회합니다.",
            parameters = {
                    @Parameter(name = "clientId", description = "클라이언트 ID", example = "2")
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = ClientDetailResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "클라이언트를 찾을 수 없음")
            }
    )
    @GetMapping("/{clientId}")
    public ResponseEntity<ApiResponse<ClientDetailResponse>> getClientDetail(
            @PathVariable Long clientId) {
        ClientDetailResponse detail = clientService.getClientDetail(clientId);
        return ResponseEntity.ok(ApiResponse.success("클라이언트 상세 조회 성공", detail));
    }

    // ✅ 3. 클라이언트 삭제
    @Operation(
            summary = "클라이언트 삭제",
            description = "특정 클라이언트(clientId)를 삭제합니다. 해당 클라이언트와 연결된 보고서는 종속되지 않습니다.",
            parameters = {
                    @Parameter(name = "clientId", description = "삭제할 클라이언트 ID", example = "2")
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한이 없는 사용자입니다."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "클라이언트를 찾을 수 없음")
            }
    )
    @DeleteMapping("/{clientId}")
    public ResponseEntity<ApiResponse<Void>> deleteClient(
            @Parameter(description = "삭제할 클라이언트 ID", example = "1")
            @PathVariable Long clientId,
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        // 🔐 JWT 토큰 추출 ("Bearer " 제거)
        String token = authorizationHeader.replace("Bearer ", "").trim();

        clientService.deleteClient(clientId, token);
        return ResponseEntity.ok(ApiResponse.success("클라이언트 삭제 성공", null));
    }
    // ✅4. 클라이언트 생성
    @PostMapping("/create")
    @Operation(
            summary = "클라이언트 등록",
            description = "JWT 토큰 기반으로 새로운 클라이언트를 등록합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    public ResponseEntity<ApiResponse<ClientDetailResponse>> createClient(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ClientCreateRequest request
    ) {
        String token = authorizationHeader.replace("Bearer ", "").trim();
        ClientDetailResponse response = clientService.createClient(token, request);
        return ResponseEntity.ok(ApiResponse.success("클라이언트 등록 성공", response));
    }
    // ✅5. 클라이언트 정보 수정

    @PatchMapping("/{clientId}")
    @Operation(
            summary = "클라이언트 수정",
            description = "본인이 등록한 클라이언트 정보를 수정합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    public ResponseEntity<ApiResponse<ClientDetailResponse>> updateClient(
            @PathVariable Long clientId,
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ClientUpdateRequest request
    ) {
        String token = authorizationHeader.replace("Bearer ", "").trim();
        ClientDetailResponse response = clientService.updateClient(clientId, token, request);
        return ResponseEntity.ok(ApiResponse.success("클라이언트 수정 성공", response));
    }

}
