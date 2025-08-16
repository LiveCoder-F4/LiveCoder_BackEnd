package com.idea_l.livecoder.community;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;

    @Transactional
    public Long create(CommunityCreateRequest req) {
        CommunityPost post = new CommunityPost(
                req.title(),
                req.content(),
                req.author()
        );
        return communityRepository.save(post).getId();
    }

    @Transactional
    public void delete(Long id) {
        if (!communityRepository.existsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 게시물입니다. id=" + id);
        }
        communityRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public CommunityResponse get(Long id) {
        CommunityPost p = communityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다. id=" + id));

        return new CommunityResponse(
                p.getId(), p.getTitle(), p.getContent(), p.getAuthor(),
                p.getCreatedAt().toString());
    }
    @Transactional
    public CommunityResponse update(Long id, CommunityUpdateRequest req) {
        CommunityPost post = communityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("post not found: " + id));
        // 영속 엔티티 필드 변경 -> JPA가 플러시 때 update 쿼리 실행
        post.update(req.title(), req.content());
        return CommunityResponse.from(post);

    }
}