package com.example.welperback.service.client;

import com.example.welperback.domain.client.Client;
import com.example.welperback.domain.client.ClientStatus;
import com.example.welperback.domain.report.Report;
import com.example.welperback.domain.user.User;
import com.example.welperback.dto.client.ClientDetailDto;
import com.example.welperback.dto.client.ClientListItemDto;
import com.example.welperback.dto.client.CreateClientRequest;
import com.example.welperback.dto.report.ReportSummaryDto;
import com.example.welperback.repository.auth.UserRepository;
import com.example.welperback.repository.client.ClientRepository;
import com.example.welperback.repository.report.ReportClientRepository;
import com.example.welperback.repository.report.ReportRepository;
import com.example.welperback.service.port.ClientUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Profile("!mongo")
@RequiredArgsConstructor
public class JpaClientService implements ClientUseCase {

	private final ClientRepository clientRepository;
	private final ReportClientRepository reportClientRepository;
	private final ReportRepository reportRepository;
	private final UserRepository userRepository;

	@Override
	@Transactional(readOnly = true)
	public List<ClientListItemDto> getClientsByUserEmail(String email) {
		Long userId = userRepository.findByEmail(email)
				.map(User::getId)
				.orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
		return clientRepository.findAllByUserId(userId).stream()
				.map(this::toListDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public Long createClient(String userEmail, CreateClientRequest req) {
		if (req == null || req.name == null || req.name.isBlank()) {
			throw new IllegalArgumentException("name is required");
		}
		User receiver = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));

		Client c = new Client();
		c.setRegistrationDate(req.registrationDate != null ? req.registrationDate : LocalDate.now());
		c.setName(req.name);
		c.setBirthDate(req.birthDate);
		c.setPhoneNumber(req.phoneNumber);
		c.setAddress(req.address);
		c.setSex(mapGenderToKorean(req.sex));
		c.setReferralSource(mapReferralToKorean(req.referralSource));
		c.setRequestContent(req.requestContent);
		c.setStatus(ClientStatus.COUNSELING);
		c.setReceivedBy(receiver);

		return clientRepository.save(c).getId();
	}

	@Override
	@Transactional
	public void deleteClient(Long clientId) {
		reportClientRepository.deleteAllByClientId(clientId);
		clientRepository.deleteById(clientId);
	}

	@Override
	@Transactional(readOnly = true)
	public ClientDetailDto getClientDetail(Long clientId) {
		Client c = clientRepository.findById(clientId)
				.orElseThrow(() -> new IllegalArgumentException("Client not found: " + clientId));

		List<Long> reportIds = reportClientRepository.findReportIdsByClientId(clientId);
		List<Report> reports = reportIds.isEmpty() ? List.of() : reportRepository.findAllById(reportIds);

		return ClientDetailDto.builder()
				.clientId(c.getId())
				.name(c.getName())
				.birthDate(c.getBirthDate())
				.phoneNumber(c.getPhoneNumber())
				.registrationDate(c.getRegistrationDate())
				.address(c.getAddress())
				.sex("여".equals(c.getSex()) ? com.example.welperback.domain.client.ClientSex.FEMALE : com.example.welperback.domain.client.ClientSex.MALE)
				.referralSource(mapReferralEnum(c.getReferralSource()))
				.status(c.getStatus())
				.requestContent(c.getRequestContent())
				.reports(reports.stream().map(r -> ReportSummaryDto.builder()
						.reportId(r.getId())
						.title(r.getTitle())
						.createdAt(r.getCreatedAt())
						.authorId(r.getAuthor() != null ? r.getAuthor().getId() : null)
						.build()).collect(Collectors.toList()))
				.build();
	}

	private ClientListItemDto toListDto(Client c) {
		return ClientListItemDto.builder()
				.clientId(c.getId())
				.name(c.getName())
				.birthDate(c.getBirthDate())
				.phoneNumber(c.getPhoneNumber())
				.registrationDate(c.getRegistrationDate())
				.status(c.getStatus())
				.build();
	}

	private String mapGenderToKorean(String sex) {
		if (sex == null) return null;
		if ("MALE".equalsIgnoreCase(sex) || "M".equalsIgnoreCase(sex)) return "남";
		if ("FEMALE".equalsIgnoreCase(sex) || "F".equalsIgnoreCase(sex)) return "여";
		return sex;
	}

	private String mapReferralToKorean(String ref) {
		if (ref == null) return null;
		switch (ref.toLowerCase()) {
			case "online": return "온라인";
			case "call": return "전화";
			case "visit": return "방문";
			case "other": return "기타";
			default: return ref;
		}
	}

	private com.example.welperback.domain.client.ReferralSource mapReferralEnum(String v) {
		if (v == null) return com.example.welperback.domain.client.ReferralSource.OTHER;
		switch (v) {
			case "온라인": return com.example.welperback.domain.client.ReferralSource.ONLINE;
			case "전화": return com.example.welperback.domain.client.ReferralSource.CALL;
			case "방문": return com.example.welperback.domain.client.ReferralSource.VISIT;
			default: return com.example.welperback.domain.client.ReferralSource.OTHER;
		}
	}
}
