package com.idea_l.livecoder.notification;

import com.idea_l.livecoder.common.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "알림", description = "알림 관련 API")
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;

    @GetMapping("/unread")
    @Operation(summary = "읽지 않은 알림 조회")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                System.err.println("[NotificationController] 인증 토큰 없음 또는 유효하지 않음");
                return ResponseEntity.status(401).build();
            }
            
            List<NotificationResponse> notifications = notificationService.getUnreadNotifications(userId);
            System.out.println("[NotificationController] 알림 조회: userId=" + userId + ", 건수=" + notifications.size());
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            System.err.println("[NotificationController] 알림 조회 실패 (500 Error): " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/{notificationId}/read")
    @Operation(summary = "알림 읽음 처리")
    public ResponseEntity<?> markAsRead(
            HttpServletRequest request,
            @PathVariable Long notificationId) {
        Long userId = getUserIdFromToken(request);
        if (userId == null) return ResponseEntity.status(401).build();
        try {
            notificationService.markAsRead(notificationId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/read-all")
    @Operation(summary = "모든 알림 읽음 처리")
    public ResponseEntity<?> markAllAsRead(HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        if (userId == null) return ResponseEntity.status(401).build();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    private Long getUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                return jwtUtil.getUserIdFromToken(token);
            }
        }
        return null;
    }
}
