package com.idea_l.livecoder.home;

import java.util.List;

public record HomeResponse(
        List<HomePostItem> recommendedPosts,
        List<HomeNewsItem> news,
        List<HomeSolvedItem> recentSolved
) {}
