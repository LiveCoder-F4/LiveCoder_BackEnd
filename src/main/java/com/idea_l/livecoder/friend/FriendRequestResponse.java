package com.idea_l.livecoder.friend;

import com.idea_l.livecoder.common.RequestStatus;

import java.time.LocalDateTime;

public record FriendRequestResponse(
        Long requestId,
        Long requesterId,
        String requesterNickname,
        Long receiverId,
        String receiverNickname,
        RequestStatus status,
        LocalDateTime requestedAt
) {
    public static FriendRequestResponse from(FriendRequest request) {
        return new FriendRequestResponse(
                request.getRequestId(),
                request.getRequester().getUserId(),
                request.getRequester().getNickname(),
                request.getReceiver().getUserId(),
                request.getReceiver().getNickname(),
                request.getStatus(),
                request.getRequestedAt()
        );
    }
}
