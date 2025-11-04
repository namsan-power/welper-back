package com.example.welperback.controller.report;

import com.example.welperback.dto.report.request.ReportCreateRequestDto;
import com.example.welperback.global.response.ApiResponse;
import com.example.welperback.service.report.ReportService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<Void>> createReport(
       @RequestPart(value ="reportDto") ReportCreateRequestDto reportDto,
       @RequestPart(value = "voiceFile") MultipartFile voiceFile
    ){
        reportService.createReport(reportDto, voiceFile);

        return ResponseEntity.ok(ApiResponse.success("보고서 생성 요청 성공", null));
    }
}
   
