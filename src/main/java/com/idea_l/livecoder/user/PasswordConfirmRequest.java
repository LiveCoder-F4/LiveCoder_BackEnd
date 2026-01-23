package com.idea_l.livecoder.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordConfirmRequest {

    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
}