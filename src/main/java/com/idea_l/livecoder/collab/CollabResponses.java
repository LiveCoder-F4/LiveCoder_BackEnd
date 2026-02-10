package com.idea_l.livecoder.collab;

import java.time.LocalDateTime;
import java.util.List;

public class CollabResponses {

    public record TeamItem(
            Long collabId,
            String title,
            Long ownerId,
            String ownerNickname,
            Long problemId,
            String visibility,
            LocalDateTime createdAt
    ) {}

    public record TeamDetail(
            TeamItem team,
            List<MemberItem> members,
            CodeDetail code,
            List<ReplyItem> replies
    ) {}

    public record MemberItem(
            Long userId,
            String nickname,
            String role,
            LocalDateTime joinedAt
    ) {}

    public record CodeDetail(
            Long codeId,
            Long authorId,
            String authorNickname,
            String language,
            String code,
            String description,
            Integer version,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    public record ReplyItem(
            Long replyId,
            Integer version,
            Long authorId,
            String authorNickname,
            String message,
            String language,
            String code,
            LocalDateTime createdAt
    ) {}

    public record InviteItem(
            Long inviteId,
            Long collabId,
            String teamTitle,
            Long inviterId,
            String inviterNickname,
            Long inviteeId,
            String inviteeNickname,
            String status,
            LocalDateTime createdAt
    ) {}
}
