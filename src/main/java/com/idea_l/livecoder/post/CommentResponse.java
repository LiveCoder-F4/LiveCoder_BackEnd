package com.idea_l.livecoder.post;

import java.time.LocalDateTime;
import java.util.List;

public record CommentResponse(
        Long commentId,
        Long userId,
        String nickname,
        String content,
        Long parentId,
        LocalDateTime createdAt,
        List<CommentResponse> replies
) {}
