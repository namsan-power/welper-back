package com.example.welperback.service;

import com.example.welperback.domain.account.User;
import com.example.welperback.domain.client.Client;
import com.example.welperback.dto.client.ClientCreateRequest;
import com.example.welperback.dto.client.ClientDetailResponse;
import com.example.welperback.dto.client.ClientListDto;
import com.example.welperback.dto.client.ClientUpdateRequest;
import com.example.welperback.global.exception.CustomException;
import com.example.welperback.global.exception.ErrorCode;
import com.example.welperback.repository.ClientRepository;
import com.example.welperback.repository.account.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    /**
     * 클라이언트 목록 조회
     * - SUPERVISOR: 모든 클라이언트
     * - CASE_MANAGER: 자신에게 배정된 클라이언트만
     */
    public List<ClientListDto> getClientList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        
        // 현재 사용자 조회
        User currentUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        List<Client> clients;
        
        // 역할에 따라 필터링
        if ("SUPERVISOR".equals(currentUser.getRole())) {
            // 팀장: 모든 클라이언트 조회
            clients = clientRepository.findAllByDeletedAtIsNullOrderByRegistrationDateDesc();
            log.info("SUPERVISOR {} retrieved all clients: {} clients", userId, clients.size());
        } else {
            // 담당자: 자신에게 배정된 클라이언트만 조회
            clients = clientRepository.findByAssignedManagerUserIdAndDeletedAtIsNullOrderByRegistrationDateDesc(userId);
            log.info("CASE_MANAGER {} retrieved assigned clients: {} clients", userId, clients.size());
        }
        
        return clients.stream()
                .map(ClientListDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 클라이언트 생성
     * - 사례번호 자동 생성 (YYYY-###)
     */
    @Transactional
    public ClientDetailResponse createClient(ClientCreateRequest request) {
        // 개인정보 동의 확인
        if (!Boolean.TRUE.equals(request.getPrivacyConsent())) {
            throw new CustomException(ErrorCode.PRIVACY_CONSENT_REQUIRED);
        }
        
        // 담당자 존재 확인
        User assignedManager = userRepository.findByUserId(request.getAssignedManagerId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_MANAGER_ID));
        
        // 사례번호 자동 생성
        String caseNumber = generateCaseNumber(request.getRegistrationDate().getYear());
        
        // 클라이언트 생성
        Client client = Client.builder()
                .caseNumber(caseNumber)
                .clientName(request.getClientName())
                .assignedManager(assignedManager)
                .caseStatus("RECEPTION")  // 초기 상태: 접수
                .registrationDate(request.getRegistrationDate())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .contactNumber(request.getContactNumber())
                .address(request.getAddress())
                .privacyConsent(request.getPrivacyConsent())
                .build();
        
        Client savedClient = clientRepository.save(client);
        
        log.info("New client created: {} ({})", savedClient.getCaseNumber(), savedClient.getClientName());
        
        return ClientDetailResponse.from(savedClient, request.getInitialNeedsSummary());
    }

    /**
     * 클라이언트 수정
     */
    @Transactional
    public ClientDetailResponse updateClient(String caseNumber, ClientUpdateRequest request) {
        // 클라이언트 조회
        Client client = clientRepository.findByCaseNumberAndDeletedAtIsNull(caseNumber)
                .orElseThrow(() -> new CustomException(ErrorCode.CLIENT_NOT_FOUND));
        
        // 담당자 변경 시 존재 확인
        if (request.getAssignedManagerId() != null) {
            User assignedManager = userRepository.findByUserId(request.getAssignedManagerId())
                    .orElseThrow(() -> new CustomException(ErrorCode.INVALID_MANAGER_ID));
            client.setAssignedManager(assignedManager);
        }
        
        // 필드 업데이트 (null이 아닌 경우만)
        if (request.getRegistrationDate() != null) {
            client.setRegistrationDate(request.getRegistrationDate());
        }
        if (request.getClientName() != null) {
            client.setClientName(request.getClientName());
        }
        if (request.getGender() != null) {
            client.setGender(request.getGender());
        }
        if (request.getBirthDate() != null) {
            client.setBirthDate(request.getBirthDate());
        }
        if (request.getContactNumber() != null) {
            client.setContactNumber(request.getContactNumber());
        }
        if (request.getAddress() != null) {
            client.setAddress(request.getAddress());
        }
        
        Client updatedClient = clientRepository.save(client);
        
        log.info("Client updated: {} ({})", updatedClient.getCaseNumber(), updatedClient.getClientName());
        
        return ClientDetailResponse.from(updatedClient, request.getInitialNeedsSummary());
    }

    /**
     * 클라이언트 상세 조회
     */
    public ClientDetailResponse getClientDetail(String caseNumber) {
        Client client = clientRepository.findByCaseNumberAndDeletedAtIsNull(caseNumber)
                .orElseThrow(() -> new CustomException(ErrorCode.CLIENT_NOT_FOUND));
        
        log.info("Client detail retrieved: {}", caseNumber);
        
        return ClientDetailResponse.from(client);
    }

    /**
     * 클라이언트 삭제 (Soft Delete)
     */
    @Transactional
    public void deleteClient(String caseNumber) {
        Client client = clientRepository.findByCaseNumberAndDeletedAtIsNull(caseNumber)
                .orElseThrow(() -> new CustomException(ErrorCode.CLIENT_NOT_FOUND));
        
        // Soft delete
        client.setDeletedAt(LocalDateTime.now());
        clientRepository.save(client);
        
        log.info("Client soft deleted: {} ({})", client.getCaseNumber(), client.getClientName());
    }

    /**
     * 사례번호 자동 생성 (YYYY-###)
     */
    private String generateCaseNumber(int year) {
        String yearPrefix = String.valueOf(year);
        
        // 해당 연도의 최신 사례번호 조회
        String latestCaseNumber = clientRepository.findLatestCaseNumberByYear(yearPrefix)
                .orElse(null);
        
        int nextSequence = 1;
        
        if (latestCaseNumber != null && latestCaseNumber.startsWith(yearPrefix + "-")) {
            // 예: "2024-005" -> "005" -> 5 -> 6
            String sequencePart = latestCaseNumber.substring(yearPrefix.length() + 1);
            try {
                nextSequence = Integer.parseInt(sequencePart) + 1;
            } catch (NumberFormatException e) {
                log.warn("Invalid case number format: {}", latestCaseNumber);
            }
        }
        
        // 형식: YYYY-###
        String caseNumber = String.format("%s-%03d", yearPrefix, nextSequence);
        
        log.info("Generated case number: {}", caseNumber);
        
        return caseNumber;
    }
}
