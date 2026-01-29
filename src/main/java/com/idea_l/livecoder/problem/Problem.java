package com.idea_l.livecoder.problem;

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
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long problemId;

    @Column(nullable = false)
    private String title;   // 문제 제목

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description; // 문제 설명

    @Column(nullable = false, columnDefinition = "TEXT")
    private String input;

    @Column(nullable = false)
    private String answer;  // 정답 (지금은 문자열로 단순화)

    public Problem(String title, String description, String answer, String input) {
        this.title = title;
        this.description = description;
        this.answer = answer;
        this.input = input;
    }
}
