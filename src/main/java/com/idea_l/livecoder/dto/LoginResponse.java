package com.idea_l.livecoder.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    
    private Long userId;
    private String username;
    private String nickname;
    private String token;
    private String message;
    
    public static LoginResponse success(Long userId, String username, String nickname, String token) {
        return new LoginResponse(userId, username, nickname, token, "로그인 성공");
    }
    
    public static LoginResponse failure(String message) {
        return new LoginResponse(null, null, null, null, message);
    }
}