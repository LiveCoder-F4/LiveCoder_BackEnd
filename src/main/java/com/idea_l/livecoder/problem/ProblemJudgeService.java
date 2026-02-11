package com.idea_l.livecoder.problem;

import com.idea_l.livecoder.problem.ProblemDTO.ProblemResponse;
import com.idea_l.livecoder.problem.docker.java.JavaJudgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProblemJudgeService {

    private final JavaJudgeService javaJudgeService;

    public boolean judgeProblem(Problems problems, String userCode, String language) throws Exception {

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
            // 런타임 에러, 시간 초과, 메모리 초과 등
            // 현재는 단순 boolean 반환이므로 false 리턴
            // 추후 에러 메시지를 반환하도록 구조 변경 필요할 수 있음
            System.out.println("Judge Failed: " + result.error);
            return false;
        }

        return result.output.trim()
                .equals(problems.getSampleOutput().trim());
    }
}
