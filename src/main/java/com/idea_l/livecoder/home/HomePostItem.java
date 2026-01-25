package com.idea_l.livecoder.home;

import java.time.LocalDateTime;

public record HomePostItem(
        Long postId,
        String title,
        Long userId,
        String nickname,
        Integer viewCount,
        Integer likeCount,
        Integer commentCount,
        LocalDateTime createdAt
) {}
