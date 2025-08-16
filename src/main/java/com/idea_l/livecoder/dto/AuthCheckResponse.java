package com.idea_l.livecoder.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthCheckResponse {
    
    private boolean isAuthenticated;
    private Long userId;
    private String username;
    private String nickname;
    
    public static AuthCheckResponse authenticated(Long userId, String username, String nickname) {
        return new AuthCheckResponse(true, userId, username, nickname);
    }
    
    public static AuthCheckResponse notAuthenticated() {
        return new AuthCheckResponse(false, null, null, null);
    }
}