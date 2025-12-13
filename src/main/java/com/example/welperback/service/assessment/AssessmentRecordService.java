package com.example.welperback.service.assessment;

import com.example.welperback.domain.assessment.AssessmentRecord;
import com.example.welperback.domain.file.DocumentFile;
import com.example.welperback.dto.assessment.AssessmentRecordResponse;
import com.example.welperback.dto.assessment.AssessmentRecordUpdateRequest;
import com.example.welperback.repository.AssessmentRecordRepository;
import com.example.welperback.repository.DocumentFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AssessmentRecordService {

    private final AssessmentRecordRepository assessmentRecordRepository;
    private final DocumentFileRepository documentFileRepository;

    /**
     * 단일 사정 기록 조회 (recordId 기준)
     */
    @Transactional(readOnly = true)
    public AssessmentRecordResponse getByRecordId(String recordId) {
        AssessmentRecord record = assessmentRecordRepository.findById(recordId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 사정 기록입니다: " + recordId)
                );

        return AssessmentRecordResponse.fromEntity(record);
    }

    /**
     * 특정 사례(caseNumber)에 속한 모든 사정 기록 조회
     */
    @Transactional(readOnly = true)
    public List<AssessmentRecordResponse> getByCaseNumber(String caseNumber) {
        return assessmentRecordRepository.findByClient_CaseNumber(caseNumber)
                .stream()
                .map(AssessmentRecordResponse::fromEntity)
                .toList();
    }

    /**
     * 사정 기록 전체 수정 (PUT)
     */
    public AssessmentRecordResponse updateFully(String recordId, AssessmentRecordUpdateRequest dto) {
        AssessmentRecord record = assessmentRecordRepository.findById(recordId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 사정 기록입니다: " + recordId)
                );

        // 기본 필드
        record.setAssessmentDate(dto.getAssessmentDate());
        record.setType(dto.getType());
        record.setChecklistData(dto.getChecklistData());
        record.setStrengthsAndResources(dto.getStrengthsAndResources());
        record.setComprehensiveOpinion(dto.getComprehensiveOpinion());

        // 파일 ID 검증 후 세팅
        record.setGenogramFilePath(
                validateAndReturnFileId(dto.getGenogramFilePath(), record)
        );
        record.setEcomapFilePath(
                validateAndReturnFileId(dto.getEcomapFilePath(), record)
        );
        record.setVoiceRecordFilePath(
                validateAndReturnFileId(dto.getVoiceRecordFilePath(), record)
        );

        AssessmentRecord saved = assessmentRecordRepository.save(record);
        return AssessmentRecordResponse.fromEntity(saved);
    }

    /**
     * 사정 기록 삭제 (하드 삭제)
     */
    public void delete(String recordId) {
        if (!assessmentRecordRepository.existsById(recordId)) {
            throw new IllegalArgumentException("존재하지 않는 사정 기록입니다: " + recordId);
        }
        assessmentRecordRepository.deleteById(recordId);
    }

    // =========================
    // 🔒 내부 헬퍼 메서드
    // =========================

    /**
     * DocumentFile ID 검증
     * - null / blank → 그대로 허용
     * - 존재 여부
     * - READY 상태
     * - 같은 caseNumber 인지
     */
    private String validateAndReturnFileId(String fileId, AssessmentRecord record) {
        if (fileId == null || fileId.isBlank()) {
            return null;
        }

        DocumentFile file = documentFileRepository.findById(fileId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 문서 파일입니다: " + fileId)
                );

        if (!"READY".equals(file.getStatus())) {
            throw new IllegalArgumentException("READY 상태가 아닌 파일입니다: " + fileId);
        }

        String caseNumber = record.getClient().getCaseNumber();
        if (!caseNumber.equals(file.getClient().getCaseNumber())) {
            throw new IllegalArgumentException("다른 케이스의 파일은 연결할 수 없습니다.");
        }

        return fileId;
    }
}
