package com.idea_l.livecoder.collab;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollabCodeReplyRepository extends JpaRepository<CollabCodeReply, Long> {
    List<CollabCodeReply> findByTeamCollabIdOrderByVersionAscCreatedAtAsc(Long collabId);
}
