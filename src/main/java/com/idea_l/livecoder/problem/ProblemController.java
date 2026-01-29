package com.idea_l.livecoder.problem;

import java.util.List;
import com.idea_l.livecoder.problem.ProblemDTO.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/problems")
public class ProblemController{

    private final ProblemService problemService;
    private final ProblemJudgeService problemJudgeService;



    @PostMapping
    public void create(@RequestBody ProblemCreateRequest request){
        problemService.create(request);
    }

    @GetMapping
    public List<ProblemResponse> getAll(){
        return problemService.getAll();
    }

    @GetMapping("/{id}")
    public Problem getOne(@PathVariable Long id){
        return problemService.getOne(id);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody ProblemUpdateRequest request){
        problemService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        problemService.delete(id);
    }

    @PostMapping("/{id}/submissions")
    public ResponseEntity<String> submit(
            @PathVariable Long id,
            @RequestBody CodeSubmitRequest request
    ) throws Exception {

        Problem problem = problemService.getOne(id);

        boolean correct =
                problemJudgeService.judgeProblem(problem, request.getCode());

        return ResponseEntity.ok(
                correct ? "CORRECT" : "WRONG"
        );
    }

}