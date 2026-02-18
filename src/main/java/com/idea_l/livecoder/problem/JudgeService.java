package com.idea_l.livecoder.problem;

import com.idea_l.livecoder.problem.docker.java.JavaJudgeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JudgeService {

    private final JavaJudgeService javaJudgeService;

    @Getter
    @AllArgsConstructor
    public static class JudgeResult {
        private boolean correct;
        private String message;
    }

    public JudgeResult judgeProblem(Problems problems, String userCode, String language) throws Exception {

        JavaJudgeService.JudgeResult result;

        if ("java".equalsIgnoreCase(language)) {
            // 메모리 제한이 null이면 기본값 256MB 사용
            int memoryLimit = problems.getMemoryLimit() != null ? problems.getMemoryLimit() : 256;

            result = javaJudgeService.judge(
                    userCode,
                    problems.getSampleInput(),
                    memoryLimit
            );
        } else {
            throw new IllegalArgumentException("지원하지 않는 언어입니다: " + language);
        }

        if (!result.success) {
            return new JudgeResult(false, result.error);
        }

        boolean correct = result.output.trim()
                .equals(problems.getSampleOutput().trim());

        return new JudgeResult(correct, correct ? "CORRECT" : "WRONG");
    }
}
