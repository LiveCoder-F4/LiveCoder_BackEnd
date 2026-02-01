package com.idea_l.livecoder.problem.ProblemDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProblemUpdateRequest{

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

}