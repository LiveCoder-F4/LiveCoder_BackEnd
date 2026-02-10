package com.idea_l.livecoder.collab;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CollabMemberRepository extends JpaRepository<CollabMember, CollabMemberId> {

    List<CollabMember> findByCollabIdOrderByJoinedAtAsc(Long collabId);

    boolean existsByCollabIdAndUserId(Long collabId, Long userId);

    Optional<CollabMember> findByCollabIdAndUserId(Long collabId, Long userId);

    void deleteByCollabIdAndUserId(Long collabId, Long userId);

    List<CollabMember> findByUserIdOrderByJoinedAtDesc(Long userId);
}
