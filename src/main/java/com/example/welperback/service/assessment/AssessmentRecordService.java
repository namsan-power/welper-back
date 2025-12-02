package com.example.welperback.service.assessment;

import com.example.welperback.domain.assessment.AssessmentRecord;
import com.example.welperback.dto.assessment.AssessmentRecordResponse;
import com.example.welperback.dto.assessment.AssessmentRecordUpdateRequest;
import com.example.welperback.repository.AssessmentRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssessmentRecordService {

    private final AssessmentRecordRepository assessmentRecordRepository;

    /**
     * 단일 사정 기록 조회 (recordId 기준)
     */
    public AssessmentRecordResponse getByRecordId(String recordId) {
        AssessmentRecord record = assessmentRecordRepository.findById(recordId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 사정 기록입니다: " + recordId)
                );
        return AssessmentRecordResponse.fromEntity(record);
    }

    /**
     * 특정 사례(caseNumber)에 속한 모든 사정 기록 조회
     * - 1차/재사정 모두 포함
     * - FE에서 type == "NEW"만 필터링해서 쓸 수도 있음
     */
    public List<AssessmentRecordResponse> getByCaseNumber(String caseNumber) {
        return assessmentRecordRepository.findByClient_CaseNumber(caseNumber).stream()
                .map(AssessmentRecordResponse::fromEntity)
                .toList();
    }

    /**
     * 보고서 전체 수정 (PUT)
     *
     * - 수정 화면에서 받은 전체 값을 그대로 반영.
     * - null 로 보내면 DB에도 null 이 저장됨(=초기화).
     */
    public AssessmentRecordResponse updateFully(String recordId, AssessmentRecordUpdateRequest dto) {
        AssessmentRecord record = assessmentRecordRepository.findById(recordId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 사정 기록입니다: " + recordId)
                );

        // 보고서에서 수정 가능한 필드 전체 덮어쓰기
        record.setAssessmentDate(dto.getAssessmentDate());
        record.setType(dto.getType());

        record.setGenogramFilePath(dto.getGenogramFilePath());
        record.setEcomapFilePath(dto.getEcomapFilePath());
        record.setVoiceRecordFilePath(dto.getVoiceRecordFilePath());

        record.setChecklistData(dto.getChecklistData());
        record.setStrengthsAndResources(dto.getStrengthsAndResources());
        record.setComprehensiveOpinion(dto.getComprehensiveOpinion());

        AssessmentRecord saved = assessmentRecordRepository.save(record);
        return AssessmentRecordResponse.fromEntity(saved);
    }

    /**
     * 사정 기록 삭제
     * - ERD에 deletedAt이 없으므로 일단 하드 삭제.
     * - 나중에 감사 추적이 필요하면 컬럼 추가 후 soft delete로 변경 가능.
     */
    public void delete(String recordId) {
        if (!assessmentRecordRepository.existsById(recordId)) {
            throw new IllegalArgumentException("존재하지 않는 사정 기록입니다: " + recordId);
        }
        assessmentRecordRepository.deleteById(recordId);
    }
}
