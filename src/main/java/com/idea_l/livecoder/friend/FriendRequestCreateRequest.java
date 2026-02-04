package com.idea_l.livecoder.friend;

import jakarta.validation.constraints.NotNull;

public record FriendRequestCreateRequest(
        @NotNull(message = "받는 사람 ID는 필수입니다.")
        Long receiverId
) {
}
