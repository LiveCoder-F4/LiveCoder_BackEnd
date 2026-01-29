package com.idea_l.livecoder.problem;

import com.idea_l.livecoder.problem.docker.java.JavaJudgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProblemJudgeService {

    private final JavaJudgeService javaJudgeService;

    public boolean judgeProblem(Problem problem, String userCode) throws Exception {

        String result = javaJudgeService.judge(
                userCode,
                problem.getInput()
        );

        return result.trim()
                .equals(problem.getAnswer().trim());
    }
}