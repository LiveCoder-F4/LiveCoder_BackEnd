package com.idea_l.livecoder.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "difficulty")
public class Difficulty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "difficulty_id")
    private Integer difficultyId;

    @Column(name = "difficulty", nullable = false, length = 45)
    private String difficulty;

    @Column(name = "difficulty_score", nullable = false)
    private Integer difficultyScore;

    @OneToMany(mappedBy = "difficulty", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Problem> problems;

}