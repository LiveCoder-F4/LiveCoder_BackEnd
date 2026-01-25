package com.idea_l.livecoder.home;

import java.time.LocalDateTime;

public record HomeNewsItem(
        Long newsId,
        String title,
        String url,
        LocalDateTime publishedAt,
        LocalDateTime createdAt
) {}
