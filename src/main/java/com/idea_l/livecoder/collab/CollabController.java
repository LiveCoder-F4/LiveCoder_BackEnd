package com.idea_l.livecoder.collab;

import com.idea_l.livecoder.common.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/collab")
public class CollabController {

    private final CollabService collabService;
    private final JwtUtil jwtUtil;

    // ===== JWT에서 userId 추출 =====
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

    // 1) 팀 생성
    @PostMapping
    public ResponseEntity<?> createTeam(HttpServletRequest request,
                                        @RequestBody @Valid CollabRequests.TeamCreateRequest req) {
        Long userId = getUserIdFromToken(request);
        if (userId == null) return ResponseEntity.status(401).build();

        Long collabId = collabService.createTeam(userId, req);
        return ResponseEntity.ok(CollabApiResponse.ok(Map.of("collabId", collabId)));
    }

    // 2) 내가 참여한 팀 목록
    @GetMapping("/my")
    public ResponseEntity<?> myTeams(HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        if (userId == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(CollabApiResponse.ok(collabService.myTeams(userId)));
    }

    // 3) 팀 상세(멤버/원본/답글)
    @GetMapping("/{collabId}")
    public ResponseEntity<?> teamDetail(HttpServletRequest request, @PathVariable Long collabId) {
        Long userId = getUserIdFromToken(request);
        if (userId == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(CollabApiResponse.ok(collabService.teamDetail(userId, collabId)));
    }

    // 4) 팀 삭제(owner)
    @DeleteMapping("/{collabId}")
    public ResponseEntity<?> deleteTeam(HttpServletRequest request, @PathVariable Long collabId) {
        Long userId = getUserIdFromToken(request);
        if (userId == null) return ResponseEntity.status(401).build();

        collabService.deleteTeam(userId, collabId);
        return ResponseEntity.ok(CollabApiResponse.ok(Map.of("collabId", collabId), "DELETED"));
    }

    // 5) 멤버 삭제(owner)
    @DeleteMapping("/{collabId}/members/{targetUserId}")
    public ResponseEntity<?> removeMember(HttpServletRequest request,
                                          @PathVariable Long collabId,
                                          @PathVariable Long targetUserId) {
        Long userId = getUserIdFromToken(request);
        if (userId == null) return ResponseEntity.status(401).build();

        collabService.removeMember(userId, collabId, targetUserId);
        return ResponseEntity.ok(CollabApiResponse.ok(Map.of("targetUserId", targetUserId), "REMOVED"));
    }

    // 6) 비밀번호 설정(owner)
    @PutMapping("/{collabId}/password")
    public ResponseEntity<?> setPassword(HttpServletRequest request,
                                         @PathVariable Long collabId,
                                         @RequestBody @Valid CollabRequests.TeamPasswordSetRequest req) {
        Long userId = getUserIdFromToken(request);
        if (userId == null) return ResponseEntity.status(401).build();

        collabService.setPassword(userId, collabId, req);
        return ResponseEntity.ok(CollabApiResponse.ok(Map.of("collabId", collabId), "UPDATED"));
    }

    // 7) 비밀번호 인증(입장용)
    @PostMapping("/{collabId}/verify")
    public ResponseEntity<?> verify(@PathVariable Long collabId,
                                    @RequestBody @Valid CollabRequests.TeamPasswordVerifyRequest req) {
        boolean ok = collabService.verifyPassword(collabId, req);
        return ResponseEntity.ok(CollabApiResponse.ok(Map.of("verified", ok)));
    }

    // 8) 원본코드 작성/수정(멤버, 원본 수정은 author만)
    @PutMapping("/{collabId}/code")
    public ResponseEntity<?> upsertCode(HttpServletRequest request,
                                        @PathVariable Long collabId,
                                        @RequestBody @Valid CollabRequests.CodeUpsertRequest req) {
        Long userId = getUserIdFromToken(request);
        if (userId == null) return ResponseEntity.status(401).build();

        Long codeId = collabService.upsertOriginalCode(userId, collabId, req);
        return ResponseEntity.ok(CollabApiResponse.ok(Map.of("codeId", codeId)));
    }

    // 9) 원본코드 삭제(author)
    @DeleteMapping("/{collabId}/code")
    public ResponseEntity<?> deleteCode(HttpServletRequest request, @PathVariable Long collabId) {
        Long userId = getUserIdFromToken(request);
        if (userId == null) return ResponseEntity.status(401).build();

        collabService.deleteOriginalCode(userId, collabId);
        return ResponseEntity.ok(CollabApiResponse.ok(Map.of("collabId", collabId), "DELETED"));
    }

    // 10) 답글 작성(멤버)
    @PostMapping("/{collabId}/replies")
    public ResponseEntity<?> createReply(HttpServletRequest request,
                                         @PathVariable Long collabId,
                                         @RequestBody @Valid CollabRequests.ReplyCreateRequest req) {
        Long userId = getUserIdFromToken(request);
        if (userId == null) return ResponseEntity.status(401).build();

        Long replyId = collabService.createReply(userId, collabId, req);
        return ResponseEntity.ok(CollabApiResponse.ok(Map.of("replyId", replyId)));
    }

    // 11) 초대 보내기(owner)
    @PostMapping("/{collabId}/invites")
    public ResponseEntity<?> sendInvite(HttpServletRequest request,
                                        @PathVariable Long collabId,
                                        @RequestBody @Valid CollabRequests.InviteCreateRequest req) {
        Long userId = getUserIdFromToken(request);
        if (userId == null) return ResponseEntity.status(401).build();

        Long inviteId = collabService.sendInvite(userId, collabId, req);
        return ResponseEntity.ok(CollabApiResponse.ok(Map.of("inviteId", inviteId)));
    }

    // 12) 받은 요청 조회
    @GetMapping("/invites/received")
    public ResponseEntity<?> receivedInvites(HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        if (userId == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(CollabApiResponse.ok(collabService.receivedInvites(userId)));
    }

    // 13) 보낸 요청 조회
    @GetMapping("/invites/sent")
    public ResponseEntity<?> sentInvites(HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        if (userId == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(CollabApiResponse.ok(collabService.sentInvites(userId)));
    }

    // 14) 초대 수락
    @PutMapping("/invites/{inviteId}/accept")
    public ResponseEntity<?> acceptInvite(HttpServletRequest request, @PathVariable Long inviteId) {
        Long userId = getUserIdFromToken(request);
        if (userId == null) return ResponseEntity.status(401).build();

        collabService.acceptInvite(userId, inviteId);
        return ResponseEntity.ok(CollabApiResponse.ok(Map.of("inviteId", inviteId), "ACCEPTED"));
    }

    // 15) 초대 거절
    @PutMapping("/invites/{inviteId}/decline")
    public ResponseEntity<?> declineInvite(HttpServletRequest request, @PathVariable Long inviteId) {
        Long userId = getUserIdFromToken(request);
        if (userId == null) return ResponseEntity.status(401).build();

        collabService.declineInvite(userId, inviteId);
        return ResponseEntity.ok(CollabApiResponse.ok(Map.of("inviteId", inviteId), "DECLINED"));
    }

    // 16) 초대 취소(보낸사람)
    @PutMapping("/invites/{inviteId}/cancel")
    public ResponseEntity<?> cancelInvite(HttpServletRequest request, @PathVariable Long inviteId) {
        Long userId = getUserIdFromToken(request);
        if (userId == null) return ResponseEntity.status(401).build();

        collabService.cancelInvite(userId, inviteId);
        return ResponseEntity.ok(CollabApiResponse.ok(Map.of("inviteId", inviteId), "CANCELED"));
    }
}
