package com.idea_l.livecoder.collab;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollabTeamRepository extends JpaRepository<CollabTeam, Long> {
    List<CollabTeam> findByOwnerUserIdOrderByCreatedAtDesc(Long ownerId);
}
