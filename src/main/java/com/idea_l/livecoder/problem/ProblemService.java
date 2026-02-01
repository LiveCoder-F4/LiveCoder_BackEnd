package com.idea_l.livecoder.problem;

import com.idea_l.livecoder.problem.Difficulty.Difficulty;
import com.idea_l.livecoder.problem.Difficulty.DifficultyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.transaction.annotation.Transactional;
import com.idea_l.livecoder.problem.ProblemDTO.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final DifficultyRepository difficultyRepository;


    public void create(ProblemCreateRequest request) {

        Difficulty difficulty = difficultyRepository.findById(request.getDifficulty_id())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 난이도"));

        Problems problems = new Problems();

                problems.setTitle(request.getTitle());
                problems.setDescription(request.getDescription());
                problems.setInputDescription(request.getInputDescription());
                problems.setOutputDescription(request.getOutputDescription());
                problems.setSampleInput(request.getSampleInput());
                problems.setSampleOutput(request.getSampleOutput());
                problems.setConstraints(request.getConstraints());
                problems.setTimeLimit(request.getTimeLimit());
                problems.setMemoryLimit(request.getMemoryLimit());
                problems.setDifficulty(difficulty);


        problemRepository.save(problems);
    }
    @Transactional(readOnly = true)
    public List<ProblemResponse> getAll() {
        return problemRepository.findAll()
                .stream()
                .map(problems -> new ProblemResponse(
                        problems.getProblem_id(),
                        problems.getTitle(),
                        problems.getDescription(),
                        problems.getInputDescription(),
                        problems.getOutputDescription(),
                        problems.getSampleInput(),
                        problems.getSampleOutput(),
                        problems.getConstraints(),
                        problems.getTimeLimit(),
                        problems.getMemoryLimit(),
                        problems.getDifficulty().getDifficulty_id(),
                        problems.getDifficulty().getName()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public ProblemResponse getOne(Long id) {

        Problems problems = problemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문제 없음"));

        return new ProblemResponse(
                problems.getProblem_id(),
                problems.getTitle(),
                problems.getDescription(),
                problems.getInputDescription(),
                problems.getOutputDescription(),
                problems.getSampleInput(),
                problems.getSampleOutput(),
                problems.getConstraints(),
                problems.getTimeLimit(),
                problems.getMemoryLimit(),
                problems.getDifficulty().getDifficulty_id(),
                problems.getDifficulty().getName()
        );
    }


    @Transactional
    public void update(Long problem_id, ProblemUpdateRequest request) {

        Difficulty difficulty = difficultyRepository.findById(request.getDifficulty_id())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 난이도"));

        Problems problems = problemRepository.findById(problem_id)
                .orElseThrow(() -> new RuntimeException("문제 없음" + problem_id));

        problems.setTitle(request.getTitle());
        problems.setDescription(request.getDescription());
        problems.setInputDescription(request.getInputDescription());
        problems.setOutputDescription(request.getOutputDescription());
        problems.setSampleInput(request.getSampleInput());
        problems.setSampleOutput(request.getSampleOutput());
        problems.setConstraints(request.getConstraints());
        problems.setTimeLimit(request.getTimeLimit());
        problems.setMemoryLimit(request.getMemoryLimit());
        problems.setDifficulty(difficulty);
    }

    public void delete(Long problem_id) {
        Problems problems = problemRepository.findById(problem_id)
                .orElseThrow(()-> new RuntimeException("문제 없음" + problem_id));

        problemRepository.delete(problems);
    }
}
