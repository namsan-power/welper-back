package com.example.welperback.service.ai;

import com.example.welperback.domain.assessment.AssessmentRecord;
import com.example.welperback.domain.client.Client;
import com.example.welperback.domain.file.DocumentFile;
import com.example.welperback.dto.ai.AiServicePlanStatusResponse;
import com.example.welperback.dto.ai.ServicePlanAiRequestDto;
import com.example.welperback.dto.ai.ServicePlanSupervisionRequestDto;
import com.example.welperback.dto.ai.ServicePlanSupervisionResponse;
import com.example.welperback.global.exception.CustomException;
import com.example.welperback.global.exception.ErrorCode;
import com.example.welperback.repository.AssessmentRecordRepository;
import com.example.welperback.repository.ClientRepository;
import com.example.welperback.repository.DocumentFileRepository;
import com.example.welperback.service.ai.store.ServicePlanAiJobStore;
import com.example.welperback.service.ai.store.ServicePlanAiJobStore.AiJob;
import com.example.welperback.service.ai.store.ServicePlanSupervisionAiJobStore;
import com.example.welperback.service.ai.worker.ServicePlanAiJobRunner;
import com.example.welperback.service.ai.worker.ServicePlanSupervisionAiJobRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServicePlanAiService {

    private final ClientRepository clientRepository;
    private final AssessmentRecordRepository assessmentRecordRepository;
    private final DocumentFileRepository documentFileRepository;

    private final ServicePlanAiJobStore jobStore;
    private final ServicePlanAiJobRunner jobRunner;
    private final ServicePlanSupervisionAiJobStore SuperjobStore;
    private final ServicePlanSupervisionAiJobRunner SuperjobRunner;

    private static final Logger log = LoggerFactory.getLogger(ServicePlanAiService.class);

    /**
     * (1) 계획서 초안 생성 요청
     * - caseNumber로 Client + 최신 1차 사정(NEW) 조회
     * - 사정 결과 기반 payload 생성 후 job 등록 + 큐 제출
     */
    public AiServicePlanStatusResponse requestPlanDraft(ServicePlanAiRequestDto dto) {
        String caseNumber = dto.caseNumber();

        if (caseNumber == null || caseNumber.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        Client client = clientRepository.findById(caseNumber)
                .orElseThrow(() -> new CustomException(ErrorCode.CLIENT_NOT_FOUND));

        if (client.getPrivacyConsent() == null || !client.getPrivacyConsent()) {
            throw new CustomException(ErrorCode.PRIVACY_CONSENT_REQUIRED);
        }


        // ✅ 최신 1차 사정(NEW) 1개를 가져오는 메서드가 필요
        // ✅ Optional 그대로 받는다
//       Optional<AssessmentRecord> assessment = assessmentRecordRepository.findTopByClient_CaseNumberAndTypeOrderByAssessmentDateDesc( caseNumber, "NEW" );
        AssessmentRecord assessment = assessmentRecordRepository
                .findTopByClient_CaseNumberAndTypeOrderByAssessmentDateDesc(caseNumber, "NEW")
                .orElseThrow(() -> new CustomException(ErrorCode.ASSESSMENT_RECORD_NOT_FOUND)); // 새로 추가 추천

        Map<String, Object> payload = buildPayload(client, assessment);

//        // job 등록 + 큐 제출
          jobStore.initJob(caseNumber, payload);
          jobRunner.submit(caseNumber);

        return AiServicePlanStatusResponse.builder()
                .caseNumber(caseNumber)
                .status("PROCESSING")
                .message("AI 계획서 초안 생성 작업을 등록했습니다.")
                .planDraft(null)
                .build();
    }

    /**
     * (2) polling: caseNumber 기준 상태 조회
     */
    public AiServicePlanStatusResponse getCaseStatus(String caseNumber) {
        AiJob job = jobStore.getJob(caseNumber);

        if (job == null) {
            return AiServicePlanStatusResponse.builder()
                    .caseNumber(caseNumber)
                    .status("NONE")
                    .message("해당 사례에 대한 AI 계획서 초안 요청이 없습니다.")
                    .planDraft(null)
                    .build();
        }

        String status = job.getStatus();
        String msg = switch (status) {
            case "PROCESSING" -> "AI가 계획서 초안을 생성 중입니다.";
            case "FINISHED" -> "AI 계획서 초안 생성이 완료되었습니다.";
            case "FAILED" -> "AI 생성에 실패했습니다: " + job.getErrorMessage();
            default -> "알 수 없는 상태입니다.";
        };

        return AiServicePlanStatusResponse.builder()
                .caseNumber(caseNumber)
                .status(status)
                .message(msg)
                .planDraft(job.getPlanDraft())
                .build();
    }
    private Map<String, Object> toDocumentFileMap(DocumentFile file) {
        if (file == null) {
            return null;
        }

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", file.getId());
        map.put("fileName", file.getFileName());
        map.put("contentType", file.getContentType());
        map.put("size", file.getSize());
        map.put("stage", file.getStage());
        map.put("category", file.getCategory());

        return map;
    }

    /**
     * ✅ 핵심: DB에서 사정(AssessmentRecord)을 조회해서 AI 서버로 넘길 payload 조립
     * + DocumentFile 고려(파일 상태/케이스 일치 검증)
     *
     * 주의: 현재 엔티티 필드명이 *FilePath지만, v1.3에서는 *FileId가 맞음.
     *       여기서는 해당 값들을 "DocumentFile.id"로 취급한다(임시).
     */
    private Map<String, Object> buildPayload(Client client, AssessmentRecord assessment) {

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("caseNumber", client.getCaseNumber());
        root.put("clientName", client.getClientName());
        root.put("gender", client.getGender());
        root.put("birthDate", client.getBirthDate());
        root.put("address", client.getAddress());

        Map<String, Object> assessmentMap = new LinkedHashMap<>();
        assessmentMap.put("assessmentDate", assessment.getAssessmentDate());
        assessmentMap.put("type", assessment.getType());
        assessmentMap.put("checklistData", assessment.getChecklistData());
        assessmentMap.put("strengthsAndResources", assessment.getStrengthsAndResources());
        assessmentMap.put("comprehensiveOpinion", assessment.getComprehensiveOpinion());

        // ✅ ERD 기준: DocumentFile FK 직접 사용
        assessmentMap.put("genogramFile",
                toDocumentFileMap(assessment.getGenogramFile()));
        assessmentMap.put("ecomapFile",
                toDocumentFileMap(assessment.getEcomapFile()));
        assessmentMap.put("voiceRecordFile",
                toDocumentFileMap(assessment.getVoiceRecordFile()));

        root.put("assessment", assessmentMap);

        return root;
    }


    private Map<String, Object> loadDocumentFileSafe(String caseNumber, String fileIdLikePath) {
        if (fileIdLikePath == null || fileIdLikePath.isBlank()) return null;

        // 지금은 fileIdLikePath를 DocumentFile.id로 취급
        DocumentFile file = documentFileRepository.findById(fileIdLikePath)
                .orElseThrow(() -> new CustomException(ErrorCode.DOCUMENT_NOT_FOUND));

        // 케이스 번호 일치 확인(다른 케이스 파일 연결 방지)
        if (!file.getClient().getCaseNumber().equals(caseNumber)) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        // 상태 체크
        if (!"READY".equals(file.getStatus())) {
            throw new CustomException(ErrorCode.DOCUMENT_NOT_FOUND);
        }

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", file.getId());
        m.put("fileName", file.getFileName());
        m.put("contentType", file.getContentType());
        m.put("size", file.getSize());
        m.put("storagePath", file.getStoragePath());
        m.put("stage", file.getStage());
        m.put("category", file.getCategory());
        return m;
    }

    private Map<String, Object> buildSupervisionPayload(ServicePlanSupervisionRequestDto dto) {

        Map<String, Object> root = new LinkedHashMap<>();

        root.put("caseNumber", dto.caseNumber());
        root.put("planDate", dto.planDate());
        root.put("planItems", dto.planItems()); // ✅ 그대로 전달

        return root;
    }


    public void requestSupervision(ServicePlanSupervisionRequestDto dto) {

        String caseNumber = dto.caseNumber();

        if (caseNumber == null || caseNumber.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        Map<String, Object> payload = buildSupervisionPayload(dto);

        // Job 등록 (payload 포함)
        jobStore.initJob(caseNumber, payload);

        // 비동기 AI 작업 제출
        jobRunner.submit(caseNumber);
    }
    public ServicePlanSupervisionResponse getSupervisionStatus(String caseNumber) {

        var job = SuperjobStore.getJob(caseNumber);

        if (job == null) {
            return ServicePlanSupervisionResponse.builder()
                    .caseNumber(caseNumber)
                    .status("NONE")
                    .message("슈퍼비전 요청 이력이 없습니다.")
                    .planSupervision(null)
                    .build();
        }

        String message = switch (job.getStatus()) {
            case "PROCESSING" ->
                    "AI가 슈퍼비전 코멘트를 생성 중입니다.";
            case "FINISHED" ->
                    "AI 슈퍼비전 생성이 완료되었습니다.";
            case "FAILED" ->
                    "AI 슈퍼비전 생성에 실패했습니다.";
            default ->
                    "알 수 없는 상태입니다.";
        };

        return ServicePlanSupervisionResponse.builder()
                .caseNumber(caseNumber)
                .status(job.getStatus())
                .message(message)
                .planSupervision(job.getPlanSupervision())
                .build();
    }





}
