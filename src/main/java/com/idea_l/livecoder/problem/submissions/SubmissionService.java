package com.idea_l.livecoder.problem.submissions;

import com.idea_l.livecoder.problem.ProblemJudgeService;
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
    private final ProblemJudgeService problemJudgeService;
    private final UserService userService;

    public SubmissionService(
            SubmissionRepository submissionRepository,
            ProblemService problemService,
            ProblemJudgeService problemJudgeService,
            UserService userService
    ) {
        this.submissionRepository = submissionRepository;
        this.problemService = problemService;
        this.problemJudgeService = problemJudgeService;
        this.userService = userService;
    }

    @Transactional
    public ProblemJudgeService.JudgeResult submit(Long problemId, String code, String language) throws Exception {

        User user = userService.getCurrentUser();
        if (user == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }
        Problems problems = problemService.getEntity(problemId);

        ProblemJudgeService.JudgeResult judgeResult =
                problemJudgeService.judgeProblem(problems, code, language);

        Submissions submission = new Submissions(
                user,
                problems,
                code,
                language,
                judgeResult.isCorrect()
        );

        submissionRepository.save(submission);

        return judgeResult;
    }
}
