package com.idea_l.livecoder.friend;

import jakarta.validation.constraints.NotBlank;

public record FriendMessageRequest(
        @NotBlank(message = "메시지 내용은 필수입니다.")
        String content
) {
}
