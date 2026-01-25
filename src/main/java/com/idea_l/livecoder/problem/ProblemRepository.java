package com.idea_l.livecoder.problem;
import com.idea_l.livecoder.problem.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
}
