package com.idea_l.livecoder.friend;

import com.idea_l.livecoder.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendMessageRepository extends JpaRepository<FriendMessage, Long> {
    List<FriendMessage> findByReceiverOrderByCreatedAtDesc(User receiver);
    List<FriendMessage> findBySenderOrderByCreatedAtDesc(User sender);
}