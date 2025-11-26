package com.example.welperback.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterRequest {
    
    @NotBlank(message = "사용자 ID는 필수입니다.")
    @Size(min = 4, max = 20, message = "사용자 ID는 4~20자 사이여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "사용자 ID는 영문, 숫자, -, _만 사용 가능합니다.")
    private String userId;
    
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$",
        message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
    )
    private String password;
    
    @NotBlank(message = "이름은 필수입니다.")
    private String name;
    
    @NotBlank(message = "역할은 필수입니다.")
    @Pattern(regexp = "^(SUPERVISOR|CASE_MANAGER)$", message = "역할은 SUPERVISOR 또는 CASE_MANAGER여야 합니다.")
    private String role;
    
    private String agencyName;
}

