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
    private String answer;

    public String getTitle() {
        return title;
    }

    public String getAnswer() {
        return answer;
    }

    public String getDescription() {
        return description;
    }
}