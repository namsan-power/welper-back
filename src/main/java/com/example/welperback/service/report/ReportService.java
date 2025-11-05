package com.example.welperback.service.report;

import com.example.welperback.domain.client.Client;
import com.example.welperback.domain.report.Report;
import com.example.welperback.domain.report.ReportClient;
import com.example.welperback.domain.report.ReportParticipant;
import com.example.welperback.domain.user.User;
import com.example.welperback.dto.report.request.ReportCreateRequestDto;
import com.example.welperback.dto.report.response.AIAnalysisResponseDto;
import com.example.welperback.repository.client.ClientRepository;
import com.example.welperback.repository.report.ReportClientRepository;
import com.example.welperback.repository.report.ReportParticipantRepository;
import com.example.welperback.repository.report.ReportRepository;
import com.example.welperback.repository.auth.UserRepository;
import com.example.welperback.service.common.AIService;
import com.example.welperback.service.common.FileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ReportClientRepository reportClientRepository;
    private final ReportParticipantRepository reportParticipantRepository;
    private final FileService fileService;
    private final AIService aiService;
    private final ObjectMapper objectMapper;

    @Value("${welper.server.base-url}")
    private String serverBaseUrl;

    @Transactional
    public void createReport(ReportCreateRequestDto requestDto, MultipartFile voiceFile) {

        String uniqueVoiceName = fileService.upload(voiceFile);

        byte[] voiceFileBytes;
        String originalFileName = voiceFile.getOriginalFilename();
        try {
            voiceFileBytes = voiceFile.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("'파일 알맹이(Bytes)'를 읽는 데 실패했습니다.", e);
        }

        if (requestDto.getParticipantIds() == null || requestDto.getParticipantIds().isEmpty()) {
            throw new IllegalArgumentException("참석자 ID(participantIds)는 최소 1명은 필요합니다.");
        }
        User author = userRepository.findById(requestDto.getParticipantIds().get(0))
                .orElseThrow(() -> new IllegalArgumentException("작성자 ID가 잘못됐습니다."));


        Report report = new Report(requestDto.getTitle(), author);
        report.setVoiceRecord(uniqueVoiceName);
        report.setPreviewHtml("AI 분석이 진행 중입니다...");
        report.setStatus("PENDING");

        Report savedReport = reportRepository.save(report);

        requestDto.getClientIds().forEach(clientId -> {
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new IllegalArgumentException("클라이언트 ID가 잘못됐습니다."));
            ReportClient reportClient = new ReportClient(savedReport, client);
            reportClientRepository.save(reportClient);
        });
        requestDto.getParticipantIds().forEach(participantId -> {
            User participant = userRepository.findById(participantId)
                    .orElseThrow(() -> new IllegalArgumentException("참석자 ID가 잘못됐습니다."));
            ReportParticipant reportParticipant = new ReportParticipant(savedReport, participant, "참석자");
            reportParticipantRepository.save(reportParticipant);
        });


        String callbackUrl = serverBaseUrl + "/api/v1/result/analyze/voice/" + savedReport.getId();

        aiService.analyzeVoice(voiceFileBytes, originalFileName, callbackUrl, savedReport.getId());
    }

    @Transactional
    public void updateReportWithAiAnalysis(Long reportId, AIAnalysisResponseDto aiDto) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("웹훅 오류: ID " + reportId + "번 보고서를 찾을 수 없습니다."));

        String aiJsonString;
        try {
            aiJsonString = objectMapper.writeValueAsString(aiDto);
        } catch (Exception e) {
            throw new RuntimeException("AI 응답 DTO를 JSON 문자열로 변환하는 데 실패했습니다.", e);
        }

        report.setAiAnalysisData(aiJsonString);
        report.setPreviewHtml(aiDto.getComprehensiveOpinion());
        report.setStatus("COMPLETED");

        System.out.println("--- [ReportService 로그] (웹훅) ---");
        System.out.println("ID " + reportId + "번 보고서, AI 분석 '최종' 완료. DB 업데이트.");
    }
}