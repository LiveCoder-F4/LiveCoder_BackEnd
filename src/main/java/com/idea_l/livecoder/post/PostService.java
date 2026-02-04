package com.idea_l.livecoder.post;

import com.idea_l.livecoder.user.User;
import com.idea_l.livecoder.user.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;

    public PostService(
            PostRepository postRepository,
            CommentRepository commentRepository,
            PostLikeRepository postLikeRepository,
            UserRepository userRepository
    ) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.postLikeRepository = postLikeRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // 1) 커뮤니티 목록 (공지 3개 상단 고정 + 일반글 페이지네이션)
    // =========================================================
    @Transactional(readOnly = true)
    public CommunityPostsResponse getCommunity(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = (size <= 0) ? 10 : size;

        List<PostListResponse> notices = postRepository
                .findTop3ByIsNoticeTrueOrderByCreatedAtDesc()
                .stream()
                .map(PostListResponse::from)
                .toList();

        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> postPage = postRepository.findByIsNoticeFalse(pageable);

        List<PostListResponse> items = postPage.getContent()
                .stream()
                .map(PostListResponse::from)
                .toList();

        PostPageInfo pageInfo = new PostPageInfo(
                postPage.getNumber(),
                postPage.getSize(),
                postPage.getTotalElements(),
                postPage.getTotalPages()
        );

        return new CommunityPostsResponse(notices, items, pageInfo);
    }

    // =========================================================
    // 2) 게시글 상세 (+댓글 트리) + 조회수 증가
    // =========================================================
    @Transactional
    public PostDetailResponse getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다. id=" + postId));

        // ✅ 조회수 증가
        // post.setViewCount(post.getViewCount() + 1); 오류 문제가 있을 수 있음
        //null이 들어가면 아마 터짐 0으로 보정
        Integer vc= post.getViewCount();
        post.setViewCount((vc==null ? 0 : vc)+1);

        List<Comment> comments = commentRepository.findByPostPostIdOrderByCreatedAtAsc(postId);
        List<CommentResponse> commentTree = buildCommentTree(comments);

        return new PostDetailResponse(
                post.getPostId(),
                post.getTitle(),
                post.getContent(),
                post.getUser().getUserId(),
                post.getUser().getNickname(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getCreatedAt(),
                commentTree
        );
    }

    // ✅ 안전한 트리 빌드 (record 직접 add 금지)
    // ✅ 순서 보장 트리 빌드: comments(이미 createdAt ASC) 순서대로 연결
    private List<CommentResponse> buildCommentTree(List<Comment> comments) {
        Map<Long, CommentNode> map = new HashMap<>();

        // 1) 모든 댓글을 노드로 변환해서 map에 저장
        for (Comment c : comments) {
            Long parentId = (c.getParent() == null) ? null : c.getParent().getCommentId();
            map.put(c.getCommentId(), new CommentNode(
                    c.getCommentId(),
                    c.getUser().getUserId(),
                    c.getUser().getNickname(),
                    c.getContent(),
                    parentId,
                    c.getCreatedAt()
            ));
        }

        // 2) comments 순서대로(작성순) 부모-자식 연결
        List<CommentNode> roots = new ArrayList<>();
        for (Comment c : comments) {
            CommentNode node = map.get(c.getCommentId());   // ✅ 여기서 node를 꺼내야 함
            if (node == null) continue;

            if (node.parentId == null) {
                roots.add(node);
            } else {
                CommentNode parent = map.get(node.parentId);
                if (parent != null) parent.replies.add(node);
                else roots.add(node); // parent 누락 시 루트 처리
            }
        }

        // 3) roots는 이미 작성순인데 혹시 몰라 안전 정렬
        roots.sort(Comparator.comparing(n -> n.createdAt));
        return roots.stream().map(CommentNode::toRecord).toList();
    }


    private static class CommentNode {
        Long commentId;
        Long userId;
        String nickname;
        String content;
        Long parentId;
        LocalDateTime createdAt;
        List<CommentNode> replies = new ArrayList<>();

        CommentNode(Long commentId, Long userId, String nickname, String content, Long parentId, LocalDateTime createdAt) {
            this.commentId = commentId;
            this.userId = userId;
            this.nickname = nickname;
            this.content = content;
            this.parentId = parentId;
            this.createdAt = createdAt;
        }

        CommentResponse toRecord() {
            replies.sort(Comparator.comparing(n -> n.createdAt));
            return new CommentResponse(
                    commentId, userId, nickname, content, parentId, createdAt,
                    replies.stream().map(CommentNode::toRecord).toList()
            );
        }
    }

    // =========================================================
    // 3) 댓글/대댓글 작성
    // =========================================================
    @Transactional
    public Long createComment(Long postId, CommentCreateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다. id=" + postId));

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. id=" + request.userId()));

        Comment parent = null;
        if (request.parentId() != null) {
            parent = commentRepository.findById(request.parentId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부모 댓글입니다. id=" + request.parentId()));

            // ✅ 다른 게시글 댓글에 대댓글 달기 방지
            if (!Objects.equals(parent.getPost().getPostId(), postId)) {
                throw new IllegalArgumentException("부모 댓글이 해당 게시글에 속하지 않습니다.");
            }
        }

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setParent(parent);
        comment.setContent(request.content());

        Comment saved = commentRepository.save(comment);

        // ✅ 댓글 수 증가
        // post.setCommentCount(post.getCommentCount() + 1); 오류 가능성
        //마찬가지 null

        Integer cc=post.getCommentCount();
        post.setCommentCount((cc == null ? 0 : cc)+1);


        return saved.getCommentId();
    }

    // =========================================================
    // 4) 댓글 삭제 (대댓글 포함 삭제 + commentCount 정확히 차감)
    //    - orphanRemoval=true 로 실제 삭제는 JPA가 처리
    //    - 삭제 개수 계산만 안전하게 (flat 목록 기반)
    // =========================================================
    @Transactional
    public void deleteComment(Long postId, Long commentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다. id=" + postId));

        Comment target = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다. id=" + commentId));

        if (!Objects.equals(target.getPost().getPostId(), postId)) {
            throw new IllegalArgumentException("해당 게시글의 댓글이 아닙니다.");
        }

        // ✅ 삭제 대상 id들(부모+자손) 구하기
        List<Long> deleteIds = collectDescendantIdsInclusive(postId, commentId);

        // ✅ 자식부터 삭제되게 역순 삭제 (FK 안전)
        Collections.reverse(deleteIds);
        commentRepository.deleteAllById(deleteIds);
        //count null 오류 방지 
        Integer cc = post.getCommentCount();
        int safeCc = (cc == null) ? 0 : cc;
        post.setCommentCount(Math.max(0, safeCc - deleteIds.size()));
    }

    private List<Long> collectDescendantIdsInclusive(Long postId, Long rootCommentId) {
        List<Comment> flat = commentRepository.findByPostPostIdOrderByCreatedAtAsc(postId);

        Map<Long, List<Long>> children = new HashMap<>();
        for (Comment c : flat) {
            Long pid = (c.getParent() == null) ? null : c.getParent().getCommentId();
            if (pid != null) {
                children.computeIfAbsent(pid, k -> new ArrayList<>()).add(c.getCommentId());
            }
        }

        List<Long> ids = new ArrayList<>();
        Deque<Long> stack = new ArrayDeque<>();
        stack.push(rootCommentId);

        while (!stack.isEmpty()) {
            Long cur = stack.pop();
            ids.add(cur);

            List<Long> kids = children.get(cur);
            if (kids != null) for (Long k : kids) stack.push(k);
        }
        return ids;
    }


    // =========================================================
    // 5) 좋아요 / 좋아요 취소
    // =========================================================
    @Transactional
    public void likePost(Long postId, Long userId) {
        if (postLikeRepository.existsByPostPostIdAndUserUserId(postId, userId)) return;

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다. id=" + postId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. id=" + userId));

        PostLike like = new PostLike();
        like.setPost(post);
        like.setUser(user);
        postLikeRepository.save(like);

        // post.setLikeCount(post.getLikeCount() + 1); null 오류 배제
        Integer lc= post.getLikeCount();
        post.setLikeCount((lc == null ? 0 : lc)+1);
    }

    @Transactional
    public void unlikePost(Long postId, Long userId) {
        if (!postLikeRepository.existsByPostPostIdAndUserUserId(postId, userId)) return;

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다. id=" + postId));

        postLikeRepository.deleteByPostPostIdAndUserUserId(postId, userId);

        // post.setLikeCount(Math.max(0, post.getLikeCount() - 1)); null 동일
        Integer lc = post.getLikeCount();
        post.setLikeCount(Math.max(0,(lc == null ? 0 : lc)-1));
    }

    // =========================================================
    // 6) 게시글 CRUD (공지 isNotice는 여기서 못 건드림)
    // =========================================================
    @Transactional
    public Long createPost(PostCreateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. id=" + request.userId()));

        Post post = new Post();
        post.setTitle(request.title());
        post.setContent(request.content());
        post.setUser(user);
        post.setIsNotice(false); // ✅ 일반 작성은 무조건 일반글

        return postRepository.save(post).getPostId();
    }

    @Transactional
    public PostResponse updatePost(Long postId, PostUpdateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다. id=" + postId));

        post.setTitle(request.title());
        post.setContent(request.content());

        return PostResponse.from(post);
    }

    @Transactional
    public void deletePost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new IllegalArgumentException("존재하지 않는 게시글입니다. id=" + postId);
        }
        //충돌 방지 댓글 먼저 삭제
        commentRepository.deleteByPostPostId(postId);
        //충돌 방지 게시글 좋아요 먼저 삭제
        postLikeRepository.deleteByPostPostId(postId);

        postRepository.deleteById(postId);
    }

    // =========================================================
    // 7) 공지 작성: 관리자 전용
    // =========================================================
    @Transactional
    public Long createNotice(AdminNoticeCreateRequest request) {
        User admin = userRepository.findById(request.adminUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. id=" + request.adminUserId()));

        Post post = new Post();
        post.setTitle(request.title());
        post.setContent(request.content());
        post.setUser(admin);
        post.setIsNotice(true); // ✅ 여기서만 공지 생성

        return postRepository.save(post).getPostId();
    }
}
