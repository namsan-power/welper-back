package com.example.welperback.service.ai;

import com.example.welperback.domain.assessment.AssessmentRecord;
import com.example.welperback.domain.client.Client;
import com.example.welperback.dto.ai.AiAssessmentSaveRequest;
import com.example.welperback.dto.ai.AiAssessmentStatusResponse;
import com.example.welperback.dto.ai.AssessmentAiRequestDto;
import com.example.welperback.repository.AssessmentRecordRepository;
import com.example.welperback.repository.ClientRepository;
import com.example.welperback.service.ai.polling.PollingStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssessmentAiService {

    private final PollingStore pollingStore;
    private final AssessmentRecordRepository assessmentRecordRepository;
    private final ClientRepository clientRepository;

    // 2번 API 응답용
    public record AiRequestInit(
            String requestId,
            String status,
            String message
    ) {}

    // =========================
    // 2번: AI 분석 요청 시작
    // =========================
    public AiRequestInit startAiAssessment(AssessmentAiRequestDto dto) {
        String requestId = "REQ-" + UUID.randomUUID().toString().substring(0, 8);
        pollingStore.init(requestId);

        return new AiRequestInit(
                requestId,
                "PROCESSING",
                "AI 분석을 시작했습니다."
        );
    }

    // =========================
    // 3번: AI 분석 결과 조회 (Polling)
    //  - 지금 잘 돌아가고 있으니까 그대로 유지
    // =========================
    public AiAssessmentStatusResponse getAssessmentStatus(String requestId) {

        var status = pollingStore.get(requestId);
        if (status == null) {
            return AiAssessmentStatusResponse.builder()
                    .status("NOT_FOUND")
                    .message("해당 요청 ID를 찾을 수 없습니다.")
                    .assessment(null)
                    .build();
        }

        pollingStore.increaseProgress(requestId);
        var updated = pollingStore.get(requestId);

        if (!"FINISHED".equals(updated.state())) {
            return AiAssessmentStatusResponse.builder()
                    .status("PROCESSING")
                    .message("AI가 음성 분석을 진행 중입니다.")
                    .assessment(null)
                    .build();
        }

        Map<String, Object> assessment = buildMockAssessment();

        return AiAssessmentStatusResponse.builder()
                .status("FINISHED")
                .message(null)
                .assessment(assessment)
                .build();
    }

    // =========================
    // 4번: AI 사정 결과 DB 저장
    // =========================
    public String saveAiAssessment(AiAssessmentSaveRequest dto) {

        // 1) caseNumber로 Client 찾기
        Client client = clientRepository.findById(dto.getCaseNumber())
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 사례번호입니다: " + dto.getCaseNumber())
                );
        // → 나중에 CustomException + ErrorCode.CLIENT_NOT_FOUND로 바꿔도 됨

        // 2) recordId 생성
        String recordId = "AR-" + UUID.randomUUID().toString().substring(0, 8);

        // 3) 엔티티 빌드
        AssessmentRecord record = AssessmentRecord.builder()
                .recordId(recordId)
                .client(client)  // ★ 연관관계 주입
                .assessmentDate(dto.getAssessmentDate())
                .type(dto.getType())

                .genogramFilePath(dto.getGenogramFileId())
                .ecomapFilePath(dto.getEcomapFileId())
                .voiceRecordFilePath(dto.getVoiceRecordFileUrl())

                .checklistData(dto.getChecklistData())  // ★ 전체 assessment JSON 통으로 저장
                .produceStatus("COMPLETE")

                .strengthsAndResources(dto.getStrengthsAndResources())
                .comprehensiveOpinion(dto.getComprehensiveOpinion())
                .build();

        // 4) 저장
        AssessmentRecord saved = assessmentRecordRepository.save(record);

        return saved.getRecordId();
    }


    // ======================================================
    // 3번 API 완성 응답용 Mock 데이터
    // → 여기가 data.assessment에 그대로 들어가는 JSON 구조
    // ======================================================
    private Map<String, Object> buildMockAssessment() {

        // 최상위 assessment 객체
        Map<String, Object> root = new LinkedHashMap<>();

        root.put("clientName", "김철수");
        root.put("managerName", "홍길동");
        root.put("caseNumber", "CASE-2025-001");
        root.put("assessmentDate", "2025-01-20");
        root.put("assessmentType", "NEW");

        // meetingLogs 배열
        Map<String, Object> meetingLog = new LinkedHashMap<>();
        meetingLog.put("date", "2025-01-20T14:00:00Z");
        meetingLog.put("method", "대면");
        meetingLog.put("interviewer", "홍길동");
        meetingLog.put("interviewee", "김철수");
        root.put("meetingLogs", List.of(meetingLog));

        // clientProfile
        Map<String, Object> clientProfile = new LinkedHashMap<>();
        clientProfile.put("birthDate", "1984-03-15");
        clientProfile.put("gender", "MALE");
        clientProfile.put("occupation", "무직");
        clientProfile.put("address", "서울시 강북구");
        clientProfile.put("phoneNumber", "010-1234-5678");
        clientProfile.put("protectionType", List.of("LIVELIHOOD"));
        root.put("clientProfile", clientProfile);

        // emergencyContact
        Map<String, Object> emergencyContact = new LinkedHashMap<>();
        emergencyContact.put("name", "이영희");
        emergencyContact.put("relationship", "모");
        emergencyContact.put("phoneNumber", "010-9999-1234");
        root.put("emergencyContact", emergencyContact);

        // householdType
        root.put("householdType", "SINGLE");

        // disabilityStatus (null 값 있으므로 Map.of() 쓰지 말 것!)
        Map<String, Object> disabilityStatus = new LinkedHashMap<>();
        disabilityStatus.put("hasDisability", false);
        disabilityStatus.put("type", null);
        disabilityStatus.put("grade", null);
        root.put("disabilityStatus", disabilityStatus);

        // longTermCareStatus (null 값 포함)
        Map<String, Object> longTermCareStatus = new LinkedHashMap<>();
        longTermCareStatus.put("hasCare", false);
        longTermCareStatus.put("grade", null);
        longTermCareStatus.put("gradeDetail", null);
        root.put("longTermCareStatus", longTermCareStatus);

        // housingStatus
        Map<String, Object> housingStatus = new LinkedHashMap<>();
        housingStatus.put("houseType", "HOUSE_MULTI");
        housingStatus.put("ownershipType", "MONTHLY_RENT");
        housingStatus.put("monthlyRent", 350000);
        root.put("housingStatus", housingStatus);

        // familyMembers 배열
        Map<String, Object> familyMember = new LinkedHashMap<>();
        familyMember.put("relationship", "mother");
        familyMember.put("name", "이영희");
        familyMember.put("gender", "FEMALE");
        familyMember.put("birthDate", "1960-04-02");
        familyMember.put("age", 65);
        familyMember.put("job", "무직");
        familyMember.put("isCohabiting", true);
        familyMember.put("note", "의료 필요");
        root.put("familyMembers", List.of(familyMember));

        // 파일 ID
        root.put("genogramFileId", "file_genogram_123");
        root.put("ecomapFileId", "file_ecomap_123");

        // needsAssessmentRecord 배열
        Map<String, Object> needs1 = new LinkedHashMap<>();
        needs1.put("category", "ECONOMIC");
        needs1.put("narrativeAssessment", "경제적 어려움이 지속되고 있음");
        needs1.put("needsLevel", 3);
        needs1.put("priorityRank", 1);

        Map<String, Object> needs2 = new LinkedHashMap<>();
        needs2.put("category", "HEALTH");
        needs2.put("narrativeAssessment", "우울 및 불안 증상이 관찰됨");
        needs2.put("needsLevel", 2);
        needs2.put("priorityRank", 2);

        root.put("needsAssessmentRecord", List.of(needs1, needs2));

        // scaleAssessments 배열
        Map<String, Object> scale1 = new LinkedHashMap<>();
        scale1.put("scaleName", "PHQ-9");
        scale1.put("scaleScore", "중등도 우울 (15점)");
        scale1.put("note", "정신건강센터 연계 필요");

        Map<String, Object> scale2 = new LinkedHashMap<>();
        scale2.put("scaleName", "GAD-7");
        scale2.put("scaleScore", "중등도 불안 (13점)");
        scale2.put("note", null);

        root.put("scaleAssessments", List.of(scale1, scale2));

        // assessmentOutcome
        Map<String, Object> assessmentOutcome = new LinkedHashMap<>();
        assessmentOutcome.put("topPriorityNeeds",
                List.of("경제적 안정", "정서적 안정", "주거 안전성 확보"));
        assessmentOutcome.put("clientWants", "경제 지원과 주거 문제 해결 요청");
        assessmentOutcome.put("assessedNeeds", "경제·정서적 문제가 주요 위험 요인으로 판단됨");
        assessmentOutcome.put("strengthsAndResources", "가족 지지 강함, 지역사회 네트워크 일부 존재");
        assessmentOutcome.put("limitationsAndBarriers", "정서적 불안정과 지속적 무직 상태로 인한 동기 저하");
        assessmentOutcome.put("comprehensiveOpinion", "경제적 개입 + 정신건강 지원 동시 필요");
        assessmentOutcome.put("caseManagementLevel", "GENERAL");
        root.put("assessmentOutcome", assessmentOutcome);

        return root;
    }
}
