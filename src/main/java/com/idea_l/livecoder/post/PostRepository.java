package com.idea_l.livecoder.post;

import com.idea_l.livecoder.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserUserId(Long userId);
    List<Post> findByTitleContaining(String title);
}
