package com.idea_l.livecoder.problem;

import java.util.List;
import com.idea_l.livecoder.problem.ProblemDTO.*;
import com.idea_l.livecoder.problem.submissions.SubmissionService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/problems")
public class ProblemController {

    private final ProblemService problemService;
    private final JudgeService judgeService;
    private final SubmissionService submissionService;

    @Getter
    @AllArgsConstructor
    public static class JudgeSessionInfo {
        private String code;
        private String language;
        private JudgeService.JudgeResult result;
    }

    @PostMapping
    public void create(@RequestBody ProblemCreateRequest request){
        problemService.create(request);
    }

    @GetMapping
    public List<ProblemResponse> getAll(){
        return problemService.getAll();
    }

    @GetMapping("/{problem_id}")
    public ProblemResponse getOne(@PathVariable Long problem_id) {
        return problemService.getOne(problem_id);
    }

    @PutMapping("/{problem_id}")
    public void update(@PathVariable Long problem_id, @RequestBody ProblemUpdateRequest request){
        problemService.update(problem_id, request);
    }

    @DeleteMapping("/{problem_id}")
    public void delete(@PathVariable Long problem_id){
        problemService.delete(problem_id);
    }

    /**
     * 채점 수행 및 결과 세션 저장 (DB 저장 X)
     */
    @PostMapping("/{problem_id}/judge")
    public ResponseEntity<JudgeService.JudgeResult> judge(
            @PathVariable Long problem_id,
            @RequestBody CodeSubmitRequest request,
            HttpSession session
    ) throws Exception {
        // 채점 서비스 호출
        JudgeService.JudgeResult judgeResult =
                judgeService.judgeProblem(problemService.getEntity(problem_id), request.getCode(), request.getLanguage());

        // 결과를 세션에 저장 (문제 ID를 키로 사용하여 구분)
        session.setAttribute("JUDGE_SESSION_" + problem_id,
                new JudgeSessionInfo(request.getCode(), request.getLanguage(), judgeResult));

        // 상세 결과 반환 (성공 여부, 메시지, 시간, 메모리 등 포함)
        return ResponseEntity.ok(judgeResult);
    }

    /**
     * 세션에 저장된 마지막 채점 기록을 DB에 제출
     */
    @PostMapping("/{problem_id}/submissions")
    public ResponseEntity<?> submit(
            @PathVariable Long problem_id,
            HttpSession session
    ) throws Exception {

        // 세션에서 마지막 채점 기록 조회
        JudgeSessionInfo info = (JudgeSessionInfo) session.getAttribute("JUDGE_SESSION_" + problem_id);

        if (info == null) {
            return ResponseEntity.badRequest().body("실행 기록이 없습니다. 먼저 코드를 실행해주세요.");
        }

        // 저장 (재채점 없이 세션 정보 사용)
        submissionService.submit(problem_id, info.getCode(), info.getLanguage(), info.getResult());

        // 상세 결과 반환
        return ResponseEntity.ok(info.getResult());
    }
}
