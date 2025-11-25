package com.example.welperback.service;

import com.example.welperback.domain.client.Client;
import com.example.welperback.domain.user.User;
import com.example.welperback.dto.client.ClientCreateRequest;
import com.example.welperback.dto.client.ClientResponse;
import com.example.welperback.dto.client.ClientUpdateRequest;
import com.example.welperback.global.exception.CustomException;
import com.example.welperback.global.exception.ErrorCode;
import com.example.welperback.repository.client.ClientRepository;
import com.example.welperback.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ClientResponse> getClients(Long userId) {
        return clientRepository.findByReceivedById(userId).stream()
                .map(ClientResponse::from)
                .collect(Collectors.toList());
    }

    public ClientResponse createClient(ClientCreateRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Client client = Client.builder()
                .name(request.getName())
                .birthDate(request.getBirthDate())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .sex(request.getSex())
                .referralSource(request.getReferralSource())
                .requestContent(request.getRequestContent())
                .registrationDate(request.getRegistrationDate())
                .receivedBy(user)
                .build();

        clientRepository.save(client);
        return ClientResponse.from(client);
    }

    @Transactional(readOnly = true)
    public ClientResponse getClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLIENT_NOT_FOUND));
        return ClientResponse.from(client);
    }

    public ClientResponse updateClient(Long clientId, ClientUpdateRequest request) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLIENT_NOT_FOUND));

        if (request.getName() != null) client.setName(request.getName());
        if (request.getPhoneNumber() != null) client.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddress() != null) client.setAddress(request.getAddress());
        if (request.getStatus() != null) client.setStatus(request.getStatus());
        if (request.getReferralSource() != null) client.setReferralSource(request.getReferralSource());
        if (request.getRequestContent() != null) client.setRequestContent(request.getRequestContent());

        return ClientResponse.from(client);
    }

    public void deleteClient(Long clientId) {
        if (!clientRepository.existsById(clientId)) {
            throw new CustomException(ErrorCode.CLIENT_NOT_FOUND);
        }
        clientRepository.deleteById(clientId);
    }
}
