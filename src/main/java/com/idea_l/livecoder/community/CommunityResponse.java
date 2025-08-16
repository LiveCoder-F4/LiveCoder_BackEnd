package com.idea_l.livecoder.community;

public record CommunityResponse(
        Long id,
        String title,
        String content,
        String author,
        String createdAt
) {
    public static CommunityResponse from(CommunityPost p) {
        // createdAt이 LocalDateTime이면 간단히 문자열로 바꿔서 담자
        String createdAtStr = (p.getCreatedAt() != null) ? p.getCreatedAt().toString() : null;

        return new CommunityResponse(
                p.getId(),
                p.getTitle(),
                p.getContent(),
                p.getAuthor(),
                createdAtStr
        );
    }
}