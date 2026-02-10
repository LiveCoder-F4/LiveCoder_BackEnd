package com.idea_l.livecoder.collab;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CollabCodeRepository extends JpaRepository<CollabCode, Long> {
    Optional<CollabCode> findByTeamCollabId(Long collabId);
    void deleteByTeamCollabId(Long collabId);
}
