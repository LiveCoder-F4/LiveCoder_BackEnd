package com.idea_l.livecoder.problem.submissions;

import com.idea_l.livecoder.problem.JudgeService;
import com.idea_l.livecoder.problem.ProblemService;
import com.idea_l.livecoder.problem.Problems;
import com.idea_l.livecoder.user.User;
import com.idea_l.livecoder.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final ProblemService problemService;
    private final UserService userService;

    public SubmissionService(
            SubmissionRepository submissionRepository,
            ProblemService problemService,
            UserService userService
    ) {
        this.submissionRepository = submissionRepository;
        this.problemService = problemService;
        this.userService = userService;
    }

    @Transactional
    public void submit(Long problemId, String code, String language, JudgeService.JudgeResult judgeResult) throws Exception {

        User user = userService.getCurrentUser();
        if (user == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }
        Problems problems = problemService.getEntity(problemId);

        Submissions submission = new Submissions(
                user,
                problems,
                code,
                language,
                judgeResult.getStatus(), // 정답, 틀림, 컴파일 에러 등
                judgeResult.getExecutionTime(),
                judgeResult.getMemoryUsage()
        );

        submissionRepository.save(submission);
    }

    @Transactional(readOnly = true)
    public SubmissionResponse getSubmission(Long submissionId) {
        Submissions s = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("해당 제출 내역을 찾을 수 없습니다."));

        return new SubmissionResponse(
                s.getSubmission_id(),
                s.getProblems().getProblem_id(),
                s.getProblems().getTitle(),
                s.getCode(),
                s.getLanguage(),
                s.getStatus(),
                s.getExecutionTime(),
                s.getMemoryUsage(),
                s.getSubmittedAt()
        );
    }
}
