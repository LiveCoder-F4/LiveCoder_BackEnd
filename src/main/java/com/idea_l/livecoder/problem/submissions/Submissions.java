package com.idea_l.livecoder.problem.submissions;

import com.idea_l.livecoder.problem.Problems;
import com.idea_l.livecoder.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "problem_submissions")
public class Submissions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long submission_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problems problems;

    private String code;

    private boolean correct;

    private LocalDateTime submittedAt;

    public Submissions(User user, Problems problems, String code, boolean correct){
        this.user = user;
        this.problems = problems;
        this.code = code;
        this.correct = correct;
//        this.submittedAt = submittedAt;
    }
}
