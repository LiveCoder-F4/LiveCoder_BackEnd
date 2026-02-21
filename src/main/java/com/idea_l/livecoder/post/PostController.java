package com.idea_l.livecoder.post;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // ✅ 커뮤니티 목록: 공지 3개 고정 + 일반글 페이지
    @GetMapping("/community")
    public ResponseEntity<PostApiResponse<CommunityPostsResponse>> getCommunity(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(PostApiResponse.ok(postService.getCommunity(page, size)));
    }

    // ✅ 카테고리별 최신글 5개씩 요약 (NOTICE, QUESTION, INFO)
    @GetMapping("/summary")
    public ResponseEntity<PostApiResponse<CommunitySummaryResponse>> getCommunitySummary() {
        return ResponseEntity.ok(PostApiResponse.ok(postService.getCommunitySummary()));
    }

    // ✅ 특정 카테고리의 전체 목록 (페이징)
    @GetMapping
    public ResponseEntity<PostApiResponse<PostListPageResponse>> getPostsByCategory(
            @RequestParam String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(PostApiResponse.ok(postService.getPostsByCategory(category, page, size)));
    }

    // ✅ 상세(댓글 트리 포함)
    @GetMapping("/{postId}")
    public ResponseEntity<PostApiResponse<PostDetailResponse>> getDetail(@PathVariable Long postId) {
        return ResponseEntity.ok(PostApiResponse.ok(postService.getPostDetail(postId)));
    }

    // ✅ 조회수 증가
    @PostMapping("/{postId}/view")
    public ResponseEntity<PostApiResponse<Void>> increaseViewCount(@PathVariable Long postId) {
        postService.increaseViewCount(postId);
        return ResponseEntity.ok(PostApiResponse.ok(null));
    }

    // ✅ 게시글 작성(일반글)
    @PostMapping
    public ResponseEntity<PostApiResponse<Map<String, Long>>> createPost(
            @RequestBody @Valid PostCreateRequest request
    ) {
        Long postId = postService.createPost(request);
        return ResponseEntity.ok(PostApiResponse.ok(Map.of("postId", postId)));
    }

    // ✅ 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<PostApiResponse<Map<String, Long>>> updatePost(
            @PathVariable Long postId,
            @RequestBody @Valid PostUpdateRequest request
    ) {
        postService.updatePost(postId, request);
        return ResponseEntity.ok(PostApiResponse.ok(Map.of("postId", postId)));
    }

    // ✅ 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<PostApiResponse<Map<String, Long>>> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok(PostApiResponse.ok(Map.of("postId", postId), "DELETED"));
    }

    // ✅ 댓글/대댓글 작성
    @PostMapping("/{postId}/comments")
    public ResponseEntity<PostApiResponse<Map<String, Long>>> createComment(
            @PathVariable Long postId,
            @RequestBody @Valid CommentCreateRequest request
    ) {
        Long commentId = postService.createComment(postId, request);
        return ResponseEntity.ok(PostApiResponse.ok(Map.of("commentId", commentId)));
    }

    // ✅ 댓글 수정
    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<PostApiResponse<Map<String, Long>>> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody Map<String, String> body
    ) {
        postService.updateComment(commentId, body.get("content"));
        return ResponseEntity.ok(PostApiResponse.ok(Map.of("commentId", commentId)));
    }

    // ✅ 댓글 삭제(대댓글 포함 삭제)
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<PostApiResponse<Map<String, Long>>> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        postService.deleteComment(postId, commentId);
        return ResponseEntity.ok(PostApiResponse.ok(Map.of("commentId", commentId), "DELETED"));
    }

    // ✅ 좋아요
    @PostMapping("/{postId}/likes")
    public ResponseEntity<PostApiResponse<Map<String, Long>>> likePost(
            @PathVariable Long postId
    ) {
        postService.likePost(postId);
        return ResponseEntity.ok(PostApiResponse.ok(Map.of("postId", postId), "LIKED"));
    }

    // ✅ 좋아요 취소
    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<PostApiResponse<Map<String, Long>>> unlikePost(
            @PathVariable Long postId
    ) {
        postService.unlikePost(postId);
        return ResponseEntity.ok(PostApiResponse.ok(Map.of("postId", postId), "UNLIKED"));
    }

    // ✅ 공지 작성(관리자 전용)
    @PostMapping("/admin/notices")
    public ResponseEntity<PostApiResponse<Map<String, Long>>> createNotice(
            @RequestBody @Valid AdminNoticeCreateRequest request
    ) {
        Long postId = postService.createNotice(request);
        return ResponseEntity.ok(PostApiResponse.ok(Map.of("postId", postId)));
    }
}
