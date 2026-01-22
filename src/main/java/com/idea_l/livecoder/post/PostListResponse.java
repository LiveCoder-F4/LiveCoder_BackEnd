package com.idea_l.livecoder.post;

import java.time.LocalDateTime;

public record PostListResponse(
        Long postId,
        String title,
        Long userId,
        String nickname,
        Integer viewCount,
        Integer likeCount,
        Integer commentCount,
        LocalDateTime createdAt
) {
    public static PostListResponse from(Post post) {
        return new PostListResponse(
                post.getPostId(),
                post.getTitle(),
                post.getUser().getUserId(),
                post.getUser().getNickname(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getCreatedAt()
        );
    }
}
// 목록아이템(요약)용