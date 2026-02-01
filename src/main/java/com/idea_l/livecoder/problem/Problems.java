package com.idea_l.livecoder.problem;

import com.idea_l.livecoder.problem.Difficulty.Difficulty;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "problems")
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Problems {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long problem_id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String inputDescription;

    @Column(columnDefinition = "TEXT")
    private String outputDescription;

    @Column(columnDefinition = "TEXT")
    private String sampleInput;

    @Column(columnDefinition = "TEXT")
    private String sampleOutput;

    @Column(columnDefinition = "TEXT")
    private String constraints;

    private Integer timeLimit;
    private Integer memoryLimit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "difficulty_id", nullable = false)
    private Difficulty difficulty;

    // ⭐ 생성자
    public Problems(
            String title,
            String description,
            String inputDescription,
            String outputDescription,
            String sampleInput,
            String sampleOutput,
            String constraints,
            Integer timeLimit,
            Integer memoryLimit,
            Difficulty difficulty
    ) {
        this.title = title;
        this.description = description;
        this.inputDescription = inputDescription;
        this.outputDescription = outputDescription;
        this.sampleInput = sampleInput;
        this.sampleOutput = sampleOutput;
        this.constraints = constraints;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
        this.difficulty = difficulty;
    }
}
