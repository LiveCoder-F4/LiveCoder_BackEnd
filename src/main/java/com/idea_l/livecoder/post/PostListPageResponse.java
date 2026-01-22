package com.idea_l.livecoder.post;

import java.util.List;

public record PostListPageResponse(
        List<PostListResponse> items,
        PostPageInfo pageInfo
) {}
//목록+pageinfo