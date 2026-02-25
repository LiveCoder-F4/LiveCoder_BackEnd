package com.idea_l.livecoder.notification;

import com.idea_l.livecoder.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByUserOrderByCreatedAtDesc(User user);
    List<Notification> findAllByUserAndReadStatusFalseOrderByCreatedAtDesc(User user);
}
