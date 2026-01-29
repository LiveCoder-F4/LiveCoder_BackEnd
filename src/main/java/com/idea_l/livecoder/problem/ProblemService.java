package com.idea_l.livecoder.problem;

import org.springframework.transaction.annotation.Transactional;
import com.idea_l.livecoder.problem.ProblemDTO.*;
import com.idea_l.livecoder.problem.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProblemService {

    private final ProblemRepository problemRepository;

    public ProblemService(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    public void create(ProblemCreateRequest request) {
        Problem problem = new Problem();

                problem.setTitle(request.getTitle());
                problem.setDescription(request.getDescription());
                problem.setAnswer(request.getAnswer());
                problem.setInput(request.getInput());


        problemRepository.save(problem);
    }

    public List<ProblemResponse> getAll() {
        return problemRepository.findAll()
                .stream()
                .map(problem -> new ProblemResponse(
                        problem.getProblemId(),
                        problem.getTitle(),
                        problem.getDescription()
                ))
                .toList();
    }


    public Problem getOne(Long id) {
        return problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문제 없음 " + id));
    }


    @Transactional
    public void update(Long id, ProblemUpdateRequest request) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문제 없음" + id));

        problem.setTitle(request.getTitle());
        problem.setDescription(request.getDescription());
        problem.setAnswer(request.getAnswer());
    }

    public void delete(Long id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("문제 없음" + id));

        problemRepository.delete(problem);
    }
}
