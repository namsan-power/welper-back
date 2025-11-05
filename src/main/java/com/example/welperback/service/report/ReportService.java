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

    /**
     * 보고서 생성의 모든 흐름을 관리리
     * (파일 저장 -> AI 분석 -> DB 저장)
     * @param requestDto 수동으로 입력한 DTO
     * @param voiceFile 음성파일
     */
    @Transactional //이 모든 과정은 하나의 묶음. 하나라도 실패하면 전부 취소
    public void createReport(ReportCreateRequestDto requestDto, MultipartFile voiceFile) {


        // --- 1. (파일 저장 & AI 분석) ---
        
        // 파일 서비스는 로컬 저장 후 고유 파일 이름(String)을 반환
        // String uniqueVoiceName = fileService.upload(voiceFile);  테스트 위해서 주석처리리
        //아래 코드 지워야함----------------------------------------------
        String uniqueVoiceName = "fake_voice_file_" + ".mp3";


        // AI 서비스는 파일 분석 후 AI 응답 DTO를 반환
        // AIAnalysisResponseDto aiDto = aiService.analyzeVoice(voiceFile); 테스트 위해서 주석처리리

        //Test용 가짜 DTO 임시 생성----------------------------------
        AIAnalysisResponseDto aiDto = new AIAnalysisResponseDto();

        // --- '작성자(author)' 찾기 ---
        if (requestDto.getParticipantIds() == null || requestDto.getParticipantIds().isEmpty()) {
            throw new IllegalArgumentException("참석자 ID(participantIds)는 최소 1명은 필요합니다.");
        }
        // (나중엔 '로그인한 사용자' 정보로 바꿔야 함!)
        User author = userRepository.findById(requestDto.getParticipantIds().get(0))
                .orElseThrow(() -> new IllegalArgumentException("작성자 ID가 잘못됐습니다."));

        
        // --- 2. 보고서 본체(Report Entity) 생성 및 데이터 취합 ---
        
        // DTO의 제목과 작성자 추출
        Report report = new Report(requestDto.getTitle(), author);
        
        // DB에 파일 경로 저장
        report.setVoiceRecord(uniqueVoiceName);
        
        // DB에 종합의견 저장
        report.setPreviewHtml("AI 분석이 진행 중입니다...");

        // 보고서를 먼저 DB에 임시 save (이래야 'report_id'가 생김)
        Report savedReport = reportRepository.save(report);

        // --- 4. (M:N 관계) 저장 (즉시 실행) ---
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

        // --- 5. AI 모델 호출 백그라운드로 ---

        // 결과를 돌려받은 callBackUrl 설정
        String callbackUrl = serverBaseUrl + "/api/v1/result/analyze/voice/" + savedReport.getId();

        aiService.analyzeVoice(voiceFile, callbackUrl);
    }

    /**
     * AI의 분석 결과를 받아서 임시 저장된 Report를 완료 상태로 업데이트
     * @param reportId (업데이트할 보고서 ID)
     * @param aiDto (AI가 보낸 '거대 JSON' DTO)
     */
    @Transactional
    public void updateReportWithAiAnalysis(Long reportId, AIAnalysisResponseDto aiDto) {

        // (1) DB에서 임시 저장된 보고서를 ID로 찾아온다.
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("웹훅 오류: ID " + reportId + "번 보고서를 찾을 수 없습니다."));

        // (2) AI가 준 '거대 DTO'를 'JSON 문자열'로 변환한다.
        String aiJsonString;
        try {
            aiJsonString = objectMapper.writeValueAsString(aiDto);
        } catch (Exception e) {
            throw new RuntimeException("AI 응답 DTO를 JSON 문자열로 변환하는 데 실패했습니다.", e);
        }

        // (3) 최종 데이터를 업데이트한다
        report.setAiAnalysisData(aiJsonString); // 'JSON 원본' 저장
        report.setPreviewHtml(aiDto.getComprehensiveOpinion()); // '종합 의견' 저장
        report.setStatus("COMPLETED"); // '상태'를 '완료'로 변경!

        System.out.println("--- [ReportService 로그] (웹훅) ---");
        System.out.println("ID " + reportId + "번 보고서, AI 분석 '최종' 완료. DB 업데이트.");
    }
}