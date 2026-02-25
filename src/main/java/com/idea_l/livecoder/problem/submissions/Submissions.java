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

    @Column(columnDefinition = "TEXT")
    private String code;

    private String language;

    private String status; // '정답', '틀림', '컴파일 에러' 등

    private Long executionTime; // 소요 시간 (ms)

    private Long memoryUsage; // 메모리 사용량 (KB)

    private LocalDateTime submittedAt;

    @PrePersist
    public void prePersist() {
        this.submittedAt = LocalDateTime.now();
    }

    public Submissions(User user, Problems problems, String code, String language, String status, Long executionTime, Long memoryUsage) {
        this.user = user;
        this.problems = problems;
        this.code = code;
        this.language = language;
        this.status = status;
        this.executionTime = executionTime;
        this.memoryUsage = memoryUsage;
    }
}
