package com.idea_l.livecoder.problem.ProblemDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CodeSubmitRequest {
    private String code;
    private String language;
}
