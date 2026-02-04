package com.idea_l.livecoder.friend;

import com.idea_l.livecoder.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendMessageRepository extends JpaRepository<FriendMessage, Long> {
}