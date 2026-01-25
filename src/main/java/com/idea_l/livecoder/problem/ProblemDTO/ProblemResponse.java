package com.idea_l.livecoder.problem.ProblemDTO;

import lombok.Getter;

@Getter
public class ProblemResponse{

    private Long id;
    private String title;
    private String description;

    public ProblemResponse(Long id, String title, String description){
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public Long getId(){
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}