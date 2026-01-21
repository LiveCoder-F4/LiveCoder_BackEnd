package com.idea_l.livecoder.post;

import com.idea_l.livecoder.post.PostCreateRequest;
import com.idea_l.livecoder.post.PostResponse;
import com.idea_l.livecoder.post.PostUpdateRequest;
import com.idea_l.livecoder.post.Post;
import com.idea_l.livecoder.post.PostRepository;
import com.idea_l.livecoder.user.User;
import com.idea_l.livecoder.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Long createPost(PostCreateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. id=" + request.userId()));

        Post post = new Post();
        post.setTitle(request.title());
        post.setContent(request.content());
        post.setUser(user);

        return postRepository.save(post).getPostId();
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다. id=" + id));

        return PostResponse.from(post);
    }

    @Transactional
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 게시글입니다. id=" + id);
        }
        postRepository.deleteById(id);
    }

    @Transactional
    public PostResponse updatePost(Long id, PostUpdateRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다. id=" + id));

        post.setTitle(request.title());
        post.setContent(request.content());

        return PostResponse.from(post);
    }
}
