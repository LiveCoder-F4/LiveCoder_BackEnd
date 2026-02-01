package com.idea_l.livecoder.problem;

import com.idea_l.livecoder.problem.ProblemDTO.ProblemResponse;
import com.idea_l.livecoder.problem.docker.java.JavaJudgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProblemJudgeService {

    private final JavaJudgeService javaJudgeService;

    public boolean judgeProblem(ProblemResponse response, String userCode) throws Exception {

        String result = javaJudgeService.judge(
                userCode,
                response.getSampleOutput()
        );

        return result.trim()
                .equals(response.getSampleOutput().trim());
    }
}