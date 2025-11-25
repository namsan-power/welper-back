package com.example.welperback.dto.client;

import com.example.welperback.domain.client.ClientSex;
import com.example.welperback.domain.client.ClientStatus;
import com.example.welperback.domain.client.ReferralSource;
import com.example.welperback.dto.report.ReportSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ClientDetailDto {
	private Long clientId;
	private String name;
	private LocalDate birthDate;
	private String phoneNumber;
	private LocalDate registrationDate;
	private String address;
	private ClientSex sex;
	private ReferralSource referralSource;
	private ClientStatus status;
	private String requestContent;

	private List<ReportSummaryDto> reports;
}



