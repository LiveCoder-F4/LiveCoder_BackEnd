package com.idea_l.livecoder.notification;

import com.idea_l.livecoder.common.NotificationType;
import java.time.LocalDateTime;

public record NotificationResponse(
        Long notificationId,
        NotificationType type,
        String content,
        Boolean readStatus,
        LocalDateTime createdAt
) {
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getNotificationId(),
                notification.getType(),
                notification.getContent(),
                notification.getReadStatus(),
                notification.getCreatedAt()
        );
    }
}
