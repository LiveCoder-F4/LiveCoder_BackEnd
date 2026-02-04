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
    public boolean submit(Long problemId, String code) throws Exception {

        User user = userService.getCurrentUser(); // 🔥 여기서 user_id 자동
        Problems problems = problemService.getOne(problemId);

        boolean correct =
                problemJudgeService.judgeProblem(problems, code);

        Submissions submission = new Submissions(
                user,
                problems,
                code,
                correct
        );

        submissionRepository.save(submission);

        return correct;
    }
}
