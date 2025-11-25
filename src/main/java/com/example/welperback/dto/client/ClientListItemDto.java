package com.example.welperback.dto.client;

import com.example.welperback.domain.client.ClientStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class ClientListItemDto {
	private Long clientId;
	private String name;
	private LocalDate birthDate;
	private String phoneNumber;
	private LocalDate registrationDate;
	private ClientStatus status;
}



