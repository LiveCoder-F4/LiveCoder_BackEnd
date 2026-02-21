package com.idea_l.livecoder.post;

import java.util.List;

public record CommunitySummaryResponse(
        List<PostListResponse> notices,
        List<PostListResponse> questions,
        List<PostListResponse> info
) {}
