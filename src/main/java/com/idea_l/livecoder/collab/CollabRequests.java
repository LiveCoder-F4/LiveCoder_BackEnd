package com.idea_l.livecoder.collab;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CollabRequests {

    public record TeamCreateRequest(
            @NotBlank String title,
            Long problemId, // 첨부(선택)
            @NotNull Boolean isPrivate, // true면 private_team
            String password // private일 때만 사용(평문으로 받아서 서버에서 해시)
    ) {}

    public record TeamPasswordSetRequest(
            @NotNull Boolean isPrivate,
            String password
    ) {}

    public record TeamPasswordVerifyRequest(
            @NotBlank String password
    ) {}

    public record CodeUpsertRequest(
            @NotBlank String language,
            @NotBlank String code,
            String description
    ) {}

    public record ReplyCreateRequest(
            @NotBlank String message,
            @NotBlank String language,
            @NotBlank String code
    ) {}

    public record InviteCreateRequest(
            @NotNull Long inviteeId
    ) {}
}
