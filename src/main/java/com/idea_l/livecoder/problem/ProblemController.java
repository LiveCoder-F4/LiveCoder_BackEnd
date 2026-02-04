package com.idea_l.livecoder.problem;

import java.util.List;
import com.idea_l.livecoder.problem.ProblemDTO.*;
import com.idea_l.livecoder.problem.submissions.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/problems")
public class ProblemController{

    private final ProblemService problemService;
    private final ProblemJudgeService problemJudgeService;
    private final SubmissionService submissionService;



    @PostMapping
    public void create(@RequestBody ProblemCreateRequest request){
        problemService.create(request);
    }

    @GetMapping
    public List<ProblemResponse> getAll(){
        return problemService.getAll();
    }

    @GetMapping("/{id}")
    public Problems getOne(@PathVariable Long id) {
        return problemService.getOne(id);
    }

    @PutMapping("/{problem_id}")
    public void update(@PathVariable Long problem_id, @RequestBody ProblemUpdateRequest request){
        problemService.update(problem_id, request);
    }

    @DeleteMapping("/{problem_id}")
    public void delete(@PathVariable Long problem_id){
        problemService.delete(problem_id);
    }

    @PostMapping("/{problem_id}/submissions")
    public ResponseEntity<?> submit(
            @PathVariable Long problem_id,
            @RequestBody CodeSubmitRequest request
    ) throws Exception {

        boolean correct =
                submissionService.submit(problem_id, request.getCode());

        return ResponseEntity.ok(
                correct ? "CORRECT" : "WRONG"
        );
    }

}