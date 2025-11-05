// [ 💻 1. 'AI의 전화(웹훅)'를 받을 '새 컨트롤러' ]
// (위치: controller/report/WebhookController.java)

package com.example.welperback.controller.report;

import com.example.welperback.dto.report.response.AIAnalysisResponseDto;
import com.example.welperback.service.report.ReportService; // (ReportService를 주입받음)
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/result/analyze/voice") //기본 주소
public class WebhookController {

    private final ReportService reportService;

    /**
     * AI 모델이 '분석 결과'를 '돌려주는' API
     * @param reportId (URL 경로의 {reportId})
     * @param aiDto (AI가 보낸 '거대 JSON' 결과물)
     */
    @PostMapping("/{reportId}") // ('최종 주소': POST /api/v1/result/analyze/voice/1)
    public ResponseEntity<Void> receiveAiResult(
            @PathVariable("reportId") Long reportId,
            @RequestBody AIAnalysisResponseDto aiDto // (파일이 아니라 'JSON'을 통째로 받음)
    ) {
        // (1) {reportId}번 보고서 이 AI 결과(aiDto)를 업데이트
        reportService.updateReportWithAiAnalysis(reportId, aiDto);

        // (2) AI 모델에게 잘 받았다고 200 OK 응답
        return ResponseEntity.ok().build();
    }
}