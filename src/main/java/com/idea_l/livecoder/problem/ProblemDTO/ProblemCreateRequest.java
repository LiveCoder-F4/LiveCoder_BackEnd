package com.idea_l.livecoder.problem.ProblemDTO;

import com.idea_l.livecoder.problem.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
public class ProblemCreateRequest{


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