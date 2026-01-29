package com.idea_l.livecoder.problem.ProblemDTO;

import com.idea_l.livecoder.problem.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
public class ProblemCreateRequest{


    private String title;
    private String description;
    private String answer;
    private String input;


}