package com.example.welperback.dto.client;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClientCreateRequest {
    
    @NotNull(message = "접수일은 필수입니다.")
    private LocalDate registrationDate;
    
    @NotBlank(message = "성명은 필수입니다.")
    private String clientName;
    
    @NotBlank(message = "성별은 필수입니다.")
    @Pattern(regexp = "^(남|여)$", message = "성별은 '남' 또는 '여'만 가능합니다.")
    private String gender;
    
    @NotNull(message = "생년월일은 필수입니다.")
    private LocalDate birthDate;
    
    @NotBlank(message = "연락처는 필수입니다.")
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", 
            message = "연락처 형식이 올바르지 않습니다. (예: 010-1234-5678)")
    private String contactNumber;
    
    private String address;
    
    @NotBlank(message = "접수 경로는 필수입니다.")
    private String referralSource;
    
    private String initialNeedsSummary;
    
    @NotBlank(message = "담당자 ID는 필수입니다.")
    private String assignedManagerId;
    
    @NotNull(message = "개인정보 수집 및 이용 동의는 필수입니다.")
    @AssertTrue(message = "개인정보 수집 및 이용에 동의해야 합니다.")
    private Boolean privacyConsent;
}
