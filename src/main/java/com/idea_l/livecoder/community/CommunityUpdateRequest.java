package com.idea_l.livecoder.community;

import jakarta.validation.constraints.NotBlank;

public record CommunityUpdateRequest(
        @NotBlank String title,
        @NotBlank String content
) {}