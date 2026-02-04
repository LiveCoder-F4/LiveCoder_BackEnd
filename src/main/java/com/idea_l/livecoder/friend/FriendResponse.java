package com.idea_l.livecoder.friend;

import com.idea_l.livecoder.user.User;

import java.time.LocalDateTime;

public record FriendResponse(
        Long userId,
        String nickname,
        LocalDateTime friendsSince
) {
    public static FriendResponse of(User user, LocalDateTime friendsSince) {
        return new FriendResponse(
                user.getUserId(),
                user.getNickname(),
                friendsSince
        );
    }
}
