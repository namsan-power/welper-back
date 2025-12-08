package com.example.welperback.service;

import com.example.welperback.domain.client.Client;
import com.example.welperback.domain.client.IntakeRecord;
import com.example.welperback.dto.intake.IntakeRecordCreateRequest;
import com.example.welperback.dto.intake.IntakeRecordResponse;
import com.example.welperback.dto.intake.IntakeRecordUpdateRequest;
import com.example.welperback.global.exception.CustomException;
import com.example.welperback.global.exception.ErrorCode;
import com.example.welperback.repository.ClientRepository;
import com.example.welperback.repository.IntakeRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IntakeRecordService {

    private final IntakeRecordRepository intakeRecordRepository;
    private final ClientRepository clientRepository;

    /**
     * 인테이크 기록 생성
     */
    @Transactional
    public IntakeRecordResponse createIntakeRecord(IntakeRecordCreateRequest request) {
        // Client 조회
        Client client = clientRepository.findById(request.getCaseNumber())
                .orElseThrow(() -> new CustomException(ErrorCode.CLIENT_NOT_FOUND));

        // recordId 자동 생성
        String recordId = UUID.randomUUID().toString();

        // IntakeRecord 생성
        IntakeRecord intakeRecord = IntakeRecord.builder()
                .recordId(recordId)
                .client(client)
                .receptionistId(request.getCaseNumber()) // TODO: 실제 접수자 ID로 변경 필요
                .referralSource(request.getReferralSource())
                .initialNeedsSummary(request.getInitialNeedsSummary())
                .interviewDate(request.getInterviewDate())
                .interviewType(request.getInterviewType())
                .disabilityStatus(request.getDisabilityStatus())
                .familyMembers(request.getFamilyMembers())
                .needsCategories(request.getNeedsCategories())
                .intakeResult(request.getIntakeResult())
                .build();

        // 저장
        IntakeRecord savedRecord = intakeRecordRepository.save(intakeRecord);

        // intakeResult에 따라 Client status 업데이트
        updateClientStatus(client, request.getIntakeResult());

        log.info("IntakeRecord created: recordId={}, caseNumber={}", recordId, request.getCaseNumber());

        return convertToResponse(savedRecord);
    }

    /**
     * 인테이크 기록 단건 조회
     */
    public IntakeRecordResponse getIntakeRecord(String recordId) {
        IntakeRecord intakeRecord = intakeRecordRepository.findById(recordId)
                .orElseThrow(() -> new CustomException(ErrorCode.INTAKE_RECORD_NOT_FOUND));

        return convertToResponse(intakeRecord);
    }

    /**
     * 전체 인테이크 기록 목록 조회
     */
    public List<IntakeRecordResponse> getAllIntakeRecords() {
        return intakeRecordRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 사례번호로 인테이크 기록 조회
     */
    public List<IntakeRecordResponse> getIntakeRecordsByCaseNumber(String caseNumber) {
        return intakeRecordRepository.findAllByClient_CaseNumber(caseNumber).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 인테이크 기록 수정
     */
    @Transactional
    public IntakeRecordResponse updateIntakeRecord(String recordId, IntakeRecordUpdateRequest request) {
        IntakeRecord intakeRecord = intakeRecordRepository.findById(recordId)
                .orElseThrow(() -> new CustomException(ErrorCode.INTAKE_RECORD_NOT_FOUND));

        // 필드 업데이트
        if (request.getReferralSource() != null) {
            intakeRecord.setReferralSource(request.getReferralSource());
        }
        if (request.getInitialNeedsSummary() != null) {
            intakeRecord.setInitialNeedsSummary(request.getInitialNeedsSummary());
        }
        if (request.getInterviewDate() != null) {
            intakeRecord.setInterviewDate(request.getInterviewDate());
        }
        if (request.getInterviewType() != null) {
            intakeRecord.setInterviewType(request.getInterviewType());
        }
        if (request.getDisabilityStatus() != null) {
            intakeRecord.setDisabilityStatus(request.getDisabilityStatus());
        }
        if (request.getFamilyMembers() != null) {
            intakeRecord.setFamilyMembers(request.getFamilyMembers());
        }
        if (request.getNeedsCategories() != null) {
            intakeRecord.setNeedsCategories(request.getNeedsCategories());
        }

        // intakeResult 변경 시 Client status도 업데이트
        if (request.getIntakeResult() != null && !request.getIntakeResult().equals(intakeRecord.getIntakeResult())) {
            intakeRecord.setIntakeResult(request.getIntakeResult());
            updateClientStatus(intakeRecord.getClient(), request.getIntakeResult());
        }

        IntakeRecord updatedRecord = intakeRecordRepository.save(intakeRecord);

        log.info("IntakeRecord updated: recordId={}", recordId);

        return convertToResponse(updatedRecord);
    }

    /**
     * 인테이크 기록 삭제
     */
    @Transactional
    public void deleteIntakeRecord(String recordId) {
        if (!intakeRecordRepository.existsById(recordId)) {
            throw new CustomException(ErrorCode.INTAKE_RECORD_NOT_FOUND);
        }

        intakeRecordRepository.deleteById(recordId);

        log.info("IntakeRecord deleted: recordId={}", recordId);
    }

    /**
     * IntakeRecord -> IntakeRecordResponse 변환
     */
    private IntakeRecordResponse convertToResponse(IntakeRecord intakeRecord) {
        return IntakeRecordResponse.builder()
                .recordId(intakeRecord.getRecordId())
                .caseNumber(intakeRecord.getClient().getCaseNumber())
                .clientName(intakeRecord.getClient().getClientName())
                .receptionistId(intakeRecord.getReceptionistId())
                .referralSource(intakeRecord.getReferralSource())
                .initialNeedsSummary(intakeRecord.getInitialNeedsSummary())
                .interviewDate(intakeRecord.getInterviewDate())
                .interviewType(intakeRecord.getInterviewType())
                .disabilityStatus(intakeRecord.getDisabilityStatus())
                .familyMembers(intakeRecord.getFamilyMembers())
                .needsCategories(intakeRecord.getNeedsCategories())
                .intakeResult(intakeRecord.getIntakeResult())
                .build();
    }

    /**
     * intakeResult에 따라 Client caseStatus 업데이트
     */
    private void updateClientStatus(Client client, String intakeResult) {
        if (intakeResult == null) {
            return;
        }

        switch (intakeResult) {
            case "SELECTED":
                client.setCaseStatus("SELECTED");
                break;
            case "NOT_SELECTED":
                client.setCaseStatus("NOT_SELECTED");
                break;
            default:
                // intakeResult가 없거나 다른 값이면 변경하지 않음
                break;
        }

        clientRepository.save(client);
        log.info("Client status updated: caseNumber={}, status={}", client.getCaseNumber(), client.getCaseStatus());
    }
}
