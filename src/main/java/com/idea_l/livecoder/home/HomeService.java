package com.idea_l.livecoder.home;

import com.idea_l.livecoder.post.Post;
import com.idea_l.livecoder.post.PostRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class HomeService {

    private final PostRepository postRepository;
    private final NewsRepository newsRepository;

    @PersistenceContext
    private EntityManager em;

    public HomeService(PostRepository postRepository, NewsRepository newsRepository) {
        this.postRepository = postRepository;
        this.newsRepository = newsRepository;
    }

    @Transactional(readOnly = true)
    public HomeResponse getHome(Long userId) {
        int size = 5;

        // 1) 추천 게시글: 조회수 높은 순 TOP 5 (공지 제외)
        Pageable topViewPageable = PageRequest.of(
                0,
                size,
                Sort.by(Sort.Direction.DESC, "viewCount")
                        .and(Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        List<HomePostItem> recommendedPosts = postRepository
                .findByIsNoticeFalse(topViewPageable)
                .getContent()
                .stream()
                .map(this::toHomePostItem)
                .toList();

        // 2) 뉴스: created_at 최신순 TOP 5
        List<HomeNewsItem> news = newsRepository
                .findTop5ByOrderByCreatedAtDesc()
                .stream()
                .map(this::toHomeNewsItem)
                .toList();

        // 3) 최근 푼 문제(정답 기준, 중복 문제 제거, 최신 5개)
        List<HomeSolvedItem> recentSolved = fetchRecentSolvedDistinct(userId, size);

        return new HomeResponse(recommendedPosts, news, recentSolved);
    }

    private List<HomeSolvedItem> fetchRecentSolvedDistinct(Long userId, int size) {
        // MySQL: problem_id별 최근 정답 제출 1개만 뽑고 그 중 최근 5개
        String sql = """
            SELECT t.problem_id, p.title, d.difficulty, t.solved_at
            FROM (
                SELECT s.problem_id, MAX(s.submitted_at) AS solved_at
                FROM problem_submissions s
                WHERE s.user_id = :userId
                  AND s.status = '정답'
                GROUP BY s.problem_id
                ORDER BY solved_at DESC
                LIMIT :size
            ) t
            JOIN problems p ON p.problem_id = t.problem_id
            JOIN difficulty d ON d.difficulty_id = p.difficulty_id
            ORDER BY t.solved_at DESC
        """;

        Query q = em.createNativeQuery(sql);
        q.setParameter("userId", userId);
        q.setParameter("size", size);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();

        return rows.stream().map(r -> {
            Long problemId = ((Number) r[0]).longValue();
            String title = (String) r[1];
            String difficulty = (String) r[2];

            // submitted_at이 TIMESTAMP라 보통 java.sql.Timestamp로 옴
            LocalDateTime solvedAt = null;
            if (r[3] instanceof Timestamp ts) solvedAt = ts.toLocalDateTime();
            else if (r[3] instanceof LocalDateTime ldt) solvedAt = ldt;

            return new HomeSolvedItem(problemId, title, difficulty, solvedAt);
        }).toList();
    }

    private HomePostItem toHomePostItem(Post p) {
        Integer vc = (p.getViewCount() == null) ? 0 : p.getViewCount();
        Integer lc = (p.getLikeCount() == null) ? 0 : p.getLikeCount();
        Integer cc = (p.getCommentCount() == null) ? 0 : p.getCommentCount();

        return new HomePostItem(
                p.getPostId(),
                p.getTitle(),
                p.getUser().getUserId(),
                p.getUser().getNickname(),
                vc, lc, cc,
                p.getCreatedAt()
        );
    }

    private HomeNewsItem toHomeNewsItem(News n) {
        return new HomeNewsItem(
                n.getNewsId(),
                n.getTitle(),
                n.getUrl(),
                n.getPublishedAt(),
                n.getCreatedAt()
        );
    }
}
