package com.idea_l.livecoder.friend;

import com.idea_l.livecoder.common.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "친구 관리", description = "친구 관련 API")
@RestController
@RequestMapping("/friends")
public class FriendController {

    private final FriendService friendService;
    private final JwtUtil jwtUtil;

    @Autowired
    public FriendController(FriendService friendService, JwtUtil jwtUtil) {
        this.friendService = friendService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    @Operation(summary = "친구 목록 조회", description = "현재 로그인한 사용자의 친구 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<List<FriendResponse>> getFriends(HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(friendService.getFriends(userId));
    }

    @PostMapping("/requests")
    @Operation(summary = "친구 요청", description = "다른 사용자에게 친구 요청을 보냅니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 요청 성공",
                    content = @Content(schema = @Schema(implementation = FriendRequestResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<?> sendFriendRequest(
            HttpServletRequest request,
            @Valid @RequestBody FriendRequestCreateRequest createRequest) {
        Long userId = getUserIdFromToken(request);
        if (userId == null) {
            return ResponseEntity.status(401).body("인증이 필요합니다.");
        }
        try {
            return ResponseEntity.ok(friendService.sendFriendRequest(userId, createRequest.receiverId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/requests/received")
    @Operation(summary = "받은 친구 요청 조회", description = "현재 로그인한 사용자가 받은 친구 요청 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "받은 요청 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<List<FriendRequestResponse>> getReceivedRequests(HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(friendService.getReceivedRequests(userId));
    }

    @GetMapping("/requests/sent")
    @Operation(summary = "보낸 친구 요청 조회", description = "현재 로그인한 사용자가 보낸 친구 요청 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "보낸 요청 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<List<FriendRequestResponse>> getSentRequests(HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(friendService.getSentRequests(userId));
    }

    @PutMapping("/requests/{requestId}/accept")
    @Operation(summary = "친구 요청 수락", description = "받은 친구 요청을 수락합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 요청 수락 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<?> acceptFriendRequest(
            HttpServletRequest request,
            @Parameter(description = "친구 요청 ID") @PathVariable Long requestId) {
        Long userId = getUserIdFromToken(request);
        if (userId == null) {
            return ResponseEntity.status(401).body("인증이 필요합니다.");
        }
        try {
            friendService.acceptFriendRequest(userId, requestId);
            return ResponseEntity.ok("친구 요청을 수락했습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/requests/{requestId}/reject")
    @Operation(summary = "친구 요청 거절", description = "받은 친구 요청을 거절합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 요청 거절 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<?> rejectFriendRequest(
            HttpServletRequest request,
            @Parameter(description = "친구 요청 ID") @PathVariable Long requestId) {
        Long userId = getUserIdFromToken(request);
        if (userId == null) {
            return ResponseEntity.status(401).body("인증이 필요합니다.");
        }
        try {
            friendService.rejectFriendRequest(userId, requestId);
            return ResponseEntity.ok("친구 요청을 거절했습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/requests/{requestId}")
    @Operation(summary = "친구 요청 취소", description = "보낸 친구 요청을 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 요청 취소 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<?> cancelFriendRequest(
            HttpServletRequest request,
            @Parameter(description = "친구 요청 ID") @PathVariable Long requestId) {
        Long userId = getUserIdFromToken(request);
        if (userId == null) {
            return ResponseEntity.status(401).body("인증이 필요합니다.");
        }
        try {
            friendService.cancelFriendRequest(userId, requestId);
            return ResponseEntity.ok("친구 요청을 취소했습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "친구 삭제", description = "친구 관계를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<?> deleteFriend(
            HttpServletRequest request,
            @Parameter(description = "삭제할 친구의 사용자 ID") @PathVariable Long userId) {
        Long currentUserId = getUserIdFromToken(request);
        if (currentUserId == null) {
            return ResponseEntity.status(401).body("인증이 필요합니다.");
        }
        try {
            friendService.deleteFriend(currentUserId, userId);
            return ResponseEntity.ok("친구를 삭제했습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/msg/{userId}")
    @Operation(summary = "친구 쪽지 보내기", description = "친구에게 쪽지를 보냅니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "쪽지 전송 성공",
                    content = @Content(schema = @Schema(implementation = FriendMessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<?> sendMessage(
            HttpServletRequest request,
            @Parameter(description = "받는 사람의 사용자 ID") @PathVariable Long userId,
            @Valid @RequestBody FriendMessageRequest messageRequest) {
        Long currentUserId = getUserIdFromToken(request);
        if (currentUserId == null) {
            return ResponseEntity.status(401).body("인증이 필요합니다.");
        }
        try {
            return ResponseEntity.ok(friendService.sendMessage(currentUserId, userId, messageRequest.content()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
