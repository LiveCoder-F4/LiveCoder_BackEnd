package com.idea_l.livecoder.friend;

import java.time.LocalDateTime;

public record FriendMessageResponse(
        Long messageId,
        Long senderId,
        String senderNickname,
        Long receiverId,
        String receiverNickname,
        String content,
        Boolean isRead,
        LocalDateTime readAt,
        LocalDateTime createdAt
) {
    public static FriendMessageResponse from(FriendMessage message) {
        return new FriendMessageResponse(
                message.getMessageId(),
                message.getSender().getUserId(),
                message.getSender().getNickname(),
                message.getReceiver().getUserId(),
                message.getReceiver().getNickname(),
                message.getContent(),
                message.getIsRead(),
                message.getReadAt(),
                message.getCreatedAt()
        );
    }
}
