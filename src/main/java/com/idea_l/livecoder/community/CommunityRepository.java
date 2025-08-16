package com.idea_l.livecoder.community;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<CommunityPost, Long> { }