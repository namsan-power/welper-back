package com.example.welperback.dto.ai.supervision;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AiSupervisionSearchRequest {

    @NotBlank(message = "query는 필수입니다.")
    private String query;

    @Min(value = 1, message = "topK는 1 이상이어야 합니다.")
    private Integer topK = 3;
}

