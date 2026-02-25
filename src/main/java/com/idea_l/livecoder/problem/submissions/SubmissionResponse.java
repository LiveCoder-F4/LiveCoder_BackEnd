package com.idea_l.livecoder.problem.submissions;

import java.time.LocalDateTime;

public record SubmissionResponse(
        Long submissionId,
        Long problemId,
        String problemTitle,
        String code,
        String language,
        String status,
        Long executionTime,
        Long memoryUsage,
        LocalDateTime submittedAt
) {}
