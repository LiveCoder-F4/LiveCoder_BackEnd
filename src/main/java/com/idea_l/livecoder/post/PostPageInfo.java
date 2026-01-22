package com.idea_l.livecoder.post;

public record PostPageInfo(
        int page,
        int size,
        long totalElements,
        int totalPages
) {}
