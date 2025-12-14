package com.example.welperback.service.ai;

import com.example.welperback.domain.assessment.ServicePlan;
import com.example.welperback.dto.ai.supervision.AiSupervisionCaseResult;
import com.example.welperback.dto.ai.supervision.AiSupervisionSearchRequest;
import com.example.welperback.dto.ai.supervision.AiSupervisionSearchResponse;
import com.example.welperback.dto.assessment.ServicePlanDto;
import com.example.welperback.global.exception.CustomException;
import com.example.welperback.global.exception.ErrorCode;
import com.example.welperback.repository.ServicePlanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@SuppressWarnings({"NullAway"})
public class SupervisionAiService {

    private final WebClient aiWebClient;
    private final ServicePlanRepository servicePlanRepository;

    public SupervisionAiService(
            @Qualifier("supervisionAiWebClient") WebClient aiWebClient,
            ServicePlanRepository servicePlanRepository
    ) {
        this.aiWebClient = aiWebClient;
        this.servicePlanRepository = servicePlanRepository;
    }

    /**
     * AI 서버에 질의를 보내고, 응답으로 받은 caseNumber에 대한 ServicePlan을 매핑해 반환한다.
     */
    public List<AiSupervisionSearchResponse> search(AiSupervisionSearchRequest request) {
        int topK = request.getTopK() != null && request.getTopK() > 0 ? request.getTopK() : 3;

        List<AiSupervisionCaseResult> aiResults = callAiServer(request.getQuery(), topK);

        List<AiSupervisionSearchResponse> responses = new ArrayList<>();

        for (AiSupervisionCaseResult result : aiResults) {
            ServicePlanDto planDto = null;
            if (result.getCaseNumber() != null) {
                ServicePlan plan = servicePlanRepository
                        .findFirstByClient_CaseNumberOrderByPlanDateDesc(result.getCaseNumber())
                        .orElse(null);
                if (plan != null) {
                    planDto = ServicePlanDto.from(plan);
                }
            }
            responses.add(AiSupervisionSearchResponse.builder()
                    .caseNumber(result.getCaseNumber())
                    .score(result.getScore())
                    .servicePlan(planDto)
                    .build());
        }

        return responses;
    }

    private List<AiSupervisionCaseResult> callAiServer(String query, int topK) {
        try {
            return Objects.requireNonNull(
                    aiWebClient.post()
                            .uri("/search")
                            .bodyValue(new AiSupervisionSearchRequestBody(query, topK))
                            .retrieve()
                            .bodyToFlux(AiSupervisionCaseResult.class)
                            .collectList()
                            .block()
            );
        } catch (WebClientResponseException e) {
            log.error("AI server error: status={} body={}", e.getStatusCode().value(), e.getResponseBodyAsString());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("AI server call failed: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * AI 서버 요청용 내부 DTO
     */
    private record AiSupervisionSearchRequestBody(String query, int topK) {}
}

