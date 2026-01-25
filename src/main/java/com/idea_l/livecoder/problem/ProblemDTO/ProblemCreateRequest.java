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

    public String getTitle(){
        return title;
    }

    public String getAnswer(){
        return answer;
    }

    public String getDescription(){
        return description;
    }


}