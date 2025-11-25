package com.example.welperback.service.port;

import com.example.welperback.dto.client.ClientDetailDto;
import com.example.welperback.dto.client.ClientListItemDto;
import com.example.welperback.dto.client.CreateClientRequest;

import java.util.List;

public interface ClientUseCase {
	List<ClientListItemDto> getClientsByUserEmail(String email);
	Long createClient(String userEmail, CreateClientRequest req);
	void deleteClient(Long clientId);
	ClientDetailDto getClientDetail(Long clientId);
}
