package com.example.welperback.controller;

import com.example.welperback.dto.intake.IntakeRecordCreateRequest;
import com.example.welperback.dto.intake.IntakeRecordResponse;
import com.example.welperback.dto.intake.IntakeRecordUpdateRequest;
import com.example.welperback.global.response.ApiResponse;
import com.example.welperback.service.IntakeRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/intake")
@RequiredArgsConstructor
@Tag(name = "Intake", description = "인테이크 기록 API")
public class IntakeRecordController {

    private final IntakeRecordService intakeRecordService;

    @PostMapping
    @Operation(summary = "인테이크 기록 생성", description = "새로운 인테이크 기록을 생성합니다.")
    public ApiResponse<IntakeRecordResponse> createIntakeRecord(
            @Valid @RequestBody IntakeRecordCreateRequest request) {
        IntakeRecordResponse response = intakeRecordService.createIntakeRecord(request);
        return ApiResponse.success(response);
    }

    @GetMapping("/{recordId}")
    @Operation(summary = "인테이크 기록 단건 조회", description = "recordId로 인테이크 기록을 조회합니다.")
    public ApiResponse<IntakeRecordResponse> getIntakeRecord(@PathVariable String recordId) {
        IntakeRecordResponse response = intakeRecordService.getIntakeRecord(recordId);
        return ApiResponse.success(response);
    }

    @GetMapping
    @Operation(summary = "전체 인테이크 기록 목록 조회", description = "모든 인테이크 기록을 조회합니다.")
    public ApiResponse<List<IntakeRecordResponse>> getAllIntakeRecords() {
        List<IntakeRecordResponse> responses = intakeRecordService.getAllIntakeRecords();
        return ApiResponse.success(responses);
    }

    @GetMapping("/case/{caseNumber}")
    @Operation(summary = "사례번호로 인테이크 기록 조회", description = "특정 사례의 인테이크 기록을 조회합니다.")
    public ApiResponse<List<IntakeRecordResponse>> getIntakeRecordsByCaseNumber(
            @PathVariable String caseNumber) {
        List<IntakeRecordResponse> responses = intakeRecordService.getIntakeRecordsByCaseNumber(caseNumber);
        return ApiResponse.success(responses);
    }

    @PutMapping("/{recordId}")
    @Operation(summary = "인테이크 기록 수정", description = "인테이크 기록을 수정합니다.")
    public ApiResponse<IntakeRecordResponse> updateIntakeRecord(
            @PathVariable String recordId,
            @RequestBody IntakeRecordUpdateRequest request) {
        IntakeRecordResponse response = intakeRecordService.updateIntakeRecord(recordId, request);
        return ApiResponse.success(response);
    }

    @DeleteMapping("/{recordId}")
    @Operation(summary = "인테이크 기록 삭제", description = "인테이크 기록을 삭제합니다.")
    public ApiResponse<Void> deleteIntakeRecord(@PathVariable String recordId) {
        intakeRecordService.deleteIntakeRecord(recordId);
        return ApiResponse.success(null);
    }
}
