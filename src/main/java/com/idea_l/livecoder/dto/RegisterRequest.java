package com.idea_l.livecoder.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    
    @NotBlank(message = "사용자명은 필수입니다")
    @Size(min = 3, max = 45, message = "사용자명은 3자 이상 45자 이하여야 합니다")
    private String username;
    
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 6, message = "비밀번호는 6자 이상이어야 합니다")
    private String password;
    
    @NotBlank(message = "닉네임은 필수입니다")
    @Size(max = 45, message = "닉네임은 45자 이하여야 합니다")
    private String nickname;
    
    private String bio;
    private String githubUrl;
}