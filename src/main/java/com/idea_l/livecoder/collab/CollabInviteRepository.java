package com.idea_l.livecoder.collab;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CollabInviteRepository extends JpaRepository<CollabInvite, Long> {

    List<CollabInvite> findByInviteeUserIdAndStatusOrderByCreatedAtDesc(Long inviteeId, CollabEnums.InviteStatus status);
    List<CollabInvite> findByInviterUserIdOrderByCreatedAtDesc(Long inviterId);

    Optional<CollabInvite> findByTeamCollabIdAndInviteeUserIdAndStatus(Long collabId, Long inviteeId, CollabEnums.InviteStatus status);
}
