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
        private long memoryUsage; // KB
        private long executionTime; // ms
        private String status; // 정답, 틀림, 컴파일 에러, 런타임 에러, 시간 초과, 메모리 초과
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
            String status = "런타임 에러";
            if (result.error.contains("Time Limit Exceeded")) {
                status = "시간 초과";
            } else if (result.error.contains("Memory Limit Exceeded")) {
                status = "메모리 초과";
            } else if (result.error.startsWith("Compile Error")) {
                status = "컴파일 에러";
            }
            
            return new JudgeResult(false, result.error, result.memoryUsage, result.executionTime, status);
        }

        boolean correct = result.output.trim()
                .equals(problems.getSampleOutput().trim());

        String status = correct ? "정답" : "틀림";
        String message = correct ? "CORRECT" : "WRONG";

        return new JudgeResult(correct, message, result.memoryUsage, result.executionTime, status);
    }
}
