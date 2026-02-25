package com.idea_l.livecoder.home;

import java.time.LocalDateTime;

public record HomeSolvedItem(
        Long problemId,
        Long submissionId,
        String title,
        String difficulty,
        LocalDateTime solvedAt
) {}
