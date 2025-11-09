package com.example.welperback.service.client;

import com.example.welperback.domain.client.Client;
import com.example.welperback.domain.user.User;
import com.example.welperback.dto.client.request.ClientCreateRequest;
import com.example.welperback.dto.client.request.ClientUpdateRequest;
import com.example.welperback.dto.client.response.ClientDetailResponse;
import com.example.welperback.dto.client.response.ClientListResponse;
import com.example.welperback.global.exception.CustomException;
import com.example.welperback.global.exception.ErrorCode;
import com.example.welperback.global.security.JwtTokenProvider;
import com.example.welperback.repository.auth.UserRepository;
import com.example.welperback.repository.client.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public ClientService(ClientRepository clientRepository,
                         UserRepository userRepository,
                         JwtTokenProvider jwtTokenProvider) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /** ✅ 1. 클라이언트 등록 */
    public ClientDetailResponse createClient(String token, ClientCreateRequest request) {
        String email = jwtTokenProvider.getEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        LocalDate birthDate = null;
        if (request.getBirthDate() != null && !request.getBirthDate().isBlank()) {
            birthDate = LocalDate.parse(request.getBirthDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        Client client = new Client(request.getName());
        client.setBirthDate(birthDate);
        client.setPhoneNumber(request.getPhoneNumber());
        client.setAddress(request.getAddress());
        client.setSex(request.getSex());
        client.setReferralSource(request.getReferralSource());
        client.setRequestContent(request.getRequestContent());
        client.setReceivedBy(user);

        clientRepository.save(client);
        return ClientDetailResponse.fromEntity(client);
    }

    /** ✅ 2. 클라이언트 수정 */
    public ClientDetailResponse updateClient(Long clientId, String token, ClientUpdateRequest request) {
        String email = jwtTokenProvider.getEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLIENT_NOT_FOUND));

        // 🔐 등록자만 수정 가능
        if (!client.getReceivedBy().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        // ✅ 변경 가능한 필드 업데이트
        client.updateClientInfo(
                request.getName(),
                request.getPhoneNumber(),
                request.getAddress(),
                request.getStatus(),
                request.getReferralSource(),
                request.getRequestContent()
        );

        clientRepository.save(client); // 명시적으로 flush (optional)
        return ClientDetailResponse.fromEntity(client);
    }

    /** ✅ 3. 유저별 클라이언트 목록 조회 */
    @Transactional(readOnly = true)
    public List<ClientListResponse> getClientsByUser(Long userId) {
        List<Client> clients = clientRepository.findByReceivedBy_Id(userId);
        return clients.stream()
                .map(ClientListResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /** ✅ 4. 클라이언트 상세 조회 */
    @Transactional(readOnly = true)
    public ClientDetailResponse getClientDetail(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLIENT_NOT_FOUND));
        return ClientDetailResponse.fromEntity(client);
    }

    /** ✅ 5. 클라이언트 삭제 (권한 체크 포함) */
    public void deleteClient(Long clientId, String token) {
        String email = jwtTokenProvider.getEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLIENT_NOT_FOUND));

        if (!client.getReceivedBy().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        clientRepository.delete(client);
    }
}
