package com.idea_l.livecoder.problem.ProblemDTO;

import com.idea_l.livecoder.problem.Difficulty.Difficulty;
import lombok.Getter;

@Getter
public class ProblemResponse{

    private Long problem_id;

    private String title;
    private String description;

    private String inputDescription;
    private String outputDescription;

    private String sampleInput;
    private String sampleOutput;

    private String constraints;

    private Integer timeLimit;
    private Integer memoryLimit;

    private Long difficulty_id;
    private String difficultyName;

    public ProblemResponse(Long problem_id,
                           String title,
                           String description,
                           String inputDescription,
                           String outputDescription,
                           String sampleInput,
                           String sampleOutput,
                           String constraints,
                           Integer timeLimit,
                           Integer memoryLimit,
                           Long difficulty_id,
                           String difficultyName){
        this.problem_id = problem_id;
        this.title = title;
        this.description = description;
        this.inputDescription = inputDescription;
        this.outputDescription = outputDescription;
        this.sampleInput = sampleInput;
        this.sampleOutput = sampleOutput;
        this.constraints = constraints;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
        this.difficulty_id = difficulty_id;
        this.difficultyName = difficultyName;
    }


}