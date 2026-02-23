package com.idea_l.livecoder.problem.Difficulty;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class Difficulty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long difficulty_id;

    @Column(name="difficulty")
    private String name;

}
