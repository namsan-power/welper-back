package com.example.welperback.service;

import com.example.welperback.domain.client.Client;
import com.example.welperback.domain.report.Report;
import com.example.welperback.domain.report.ReportClient;
import com.example.welperback.domain.report.ReportParticipant;
import com.example.welperback.domain.user.User;
import com.example.welperback.dto.report.ReportCreateRequest;
import com.example.welperback.dto.report.ReportResponse;
import com.example.welperback.global.exception.CustomException;
import com.example.welperback.global.exception.ErrorCode;
import com.example.welperback.repository.client.ClientRepository;
import com.example.welperback.repository.report.ReportClientRepository;
import com.example.welperback.repository.report.ReportParticipantRepository;
import com.example.welperback.repository.report.ReportRepository;
import com.example.welperback.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportClientRepository reportClientRepository;
    private final ReportParticipantRepository reportParticipantRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    public ReportResponse createReport(ReportCreateRequest request, String authorEmail) {
        User author = userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Report report = new Report(request.getTitle(), author);
        report.setVoiceRecord(request.getVoiceFileUrl());
        // Mock AI Analysis
        report.setAiAnalysisData("{\"summary\": \"AI analyzed content...\"}");
        
        reportRepository.save(report);

        // Save Clients
        if (request.getClientIds() != null) {
            List<Client> clients = clientRepository.findAllById(request.getClientIds());
            for (Client client : clients) {
                ReportClient rc = new ReportClient(report, client);
                reportClientRepository.save(rc);
            }
        }

        // Save Participants
        if (request.getParticipantIds() != null) {
            List<User> participants = userRepository.findAllById(request.getParticipantIds());
            for (User participant : participants) {
                ReportParticipant rp = new ReportParticipant(report, participant, "참석자");
                reportParticipantRepository.save(rp);
            }
        }

        return ReportResponse.from(report);
    }

    @Transactional(readOnly = true)
    public ReportResponse getReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_NOT_FOUND));
        return ReportResponse.from(report);
    }

    public void deleteReport(Long reportId) {
        if (!reportRepository.existsById(reportId)) {
            throw new CustomException(ErrorCode.REPORT_NOT_FOUND);
        }
        reportRepository.deleteById(reportId);
    }
}
