package com.idea_l.livecoder.problem;

import java.util.List;
import com.idea_l.livecoder.problem.ProblemDTO.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/problems")
public class ProblemController{

    private final ProblemService problemService;

    public ProblemController(ProblemService problemService){
        this.problemService = problemService;
    }

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

}