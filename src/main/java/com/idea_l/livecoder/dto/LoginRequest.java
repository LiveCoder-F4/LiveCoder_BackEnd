package com.idea_l.livecoder.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    
    @NotBlank(message = "사용자명 또는 이메일은 필수입니다")
    private String usernameOrEmail;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
}