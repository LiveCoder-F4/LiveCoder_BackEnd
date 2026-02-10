package com.idea_l.livecoder.collab;

import com.idea_l.livecoder.problem.ProblemRepository;
import com.idea_l.livecoder.problem.Problems;
import com.idea_l.livecoder.user.User;
import com.idea_l.livecoder.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CollabService {

    private final CollabTeamRepository teamRepo;
    private final CollabMemberRepository memberRepo;
    private final CollabCodeRepository codeRepo;
    private final CollabCodeReplyRepository replyRepo;
    private final CollabInviteRepository inviteRepo;

    private final UserRepository userRepo;
    private final ProblemRepository problemRepo;
    private final PasswordEncoder passwordEncoder;

    // =========================
    // 팀 생성
    // =========================
    @Transactional
    public Long createTeam(Long ownerId, CollabRequests.TeamCreateRequest req) {
        User owner = userRepo.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. id=" + ownerId));

        Problems problem = null;
        if (req.problemId() != null) {
            problem = problemRepo.findById(req.problemId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문제입니다. id=" + req.problemId()));
        }

        CollabTeam team = new CollabTeam();
        team.setOwner(owner);
        team.setTitle(req.title());
        team.setProblem(problem);

        if (Boolean.TRUE.equals(req.isPrivate())) {
            team.setVisibility(CollabEnums.Visibility.private_team);
            if (req.password() == null || req.password().isBlank()) {
                throw new IllegalArgumentException("비밀번호가 필요합니다.");
            }
            team.setPasswordHash(passwordEncoder.encode(req.password()));
        } else {
            team.setVisibility(CollabEnums.Visibility.public_team);
            team.setPasswordHash(null);
        }

        CollabTeam saved = teamRepo.save(team);

        // owner 자동 가입
        CollabMember ownerMember = new CollabMember();
        ownerMember.setCollabId(saved.getCollabId());
        ownerMember.setUserId(owner.getUserId());
        ownerMember.setRole(CollabEnums.MemberRole.owner);
        memberRepo.save(ownerMember);

        return saved.getCollabId();
    }

    // =========================
    // 내가 참여한 팀 목록
    // =========================
    @Transactional(readOnly = true)
    public List<CollabResponses.TeamItem> myTeams(Long userId) {
        return memberRepo.findByUserIdOrderByJoinedAtDesc(userId).stream()
                .map(m -> {
                    CollabTeam t = m.getTeam();
                    return new CollabResponses.TeamItem(
                            t.getCollabId(),
                            t.getTitle(),
                            t.getOwner().getUserId(),
                            t.getOwner().getNickname(),
                            (t.getProblem() == null ? null : t.getProblem().getProblem_id()),
                            t.getVisibility().name(),
                            t.getCreatedAt()
                    );
                })
                .toList();
    }

    // =========================
    // 팀 상세 (멤버/원본코드/답글)
    // (방 비번은 verify로 따로 처리)
    // =========================
    @Transactional(readOnly = true)
    public CollabResponses.TeamDetail teamDetail(Long userId, Long collabId) {
        if (!memberRepo.existsByCollabIdAndUserId(collabId, userId)) {
            throw new IllegalArgumentException("권한이 없습니다. (팀 멤버만 접근 가능)");
        }

        CollabTeam team = teamRepo.findById(collabId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀입니다. id=" + collabId));

        List<CollabResponses.MemberItem> members = memberRepo.findByCollabIdOrderByJoinedAtAsc(collabId).stream()
                .map(m -> new CollabResponses.MemberItem(
                        m.getUserId(),
                        m.getUser().getNickname(),
                        m.getRole().name(),
                        m.getJoinedAt()
                ))
                .toList();

        CollabResponses.CodeDetail code = codeRepo.findByTeamCollabId(collabId)
                .map(c -> new CollabResponses.CodeDetail(
                        c.getCodeId(),
                        c.getAuthor().getUserId(),
                        c.getAuthor().getNickname(),
                        c.getLanguage(),
                        c.getCode(),
                        c.getDescription(),
                        c.getVersion(),
                        c.getCreatedAt(),
                        c.getUpdatedAt()
                ))
                .orElse(null);

        List<CollabResponses.ReplyItem> replies = replyRepo.findByTeamCollabIdOrderByVersionAscCreatedAtAsc(collabId).stream()
                .map(r -> new CollabResponses.ReplyItem(
                        r.getReplyId(),
                        r.getVersion(),
                        r.getAuthor().getUserId(),
                        r.getAuthor().getNickname(),
                        r.getMessage(),
                        r.getLanguage(),
                        r.getCode(),
                        r.getCreatedAt()
                ))
                .toList();

        CollabResponses.TeamItem teamItem = new CollabResponses.TeamItem(
                team.getCollabId(),
                team.getTitle(),
                team.getOwner().getUserId(),
                team.getOwner().getNickname(),
                (team.getProblem() == null ? null : team.getProblem().getProblem_id()),
                team.getVisibility().name(),
                team.getCreatedAt()
        );

        return new CollabResponses.TeamDetail(teamItem, members, code, replies);
    }

    // =========================
    // 팀 삭제 (owner만)
    // =========================
    @Transactional
    public void deleteTeam(Long userId, Long collabId) {
        CollabTeam team = teamRepo.findById(collabId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀입니다. id=" + collabId));

        if (!team.getOwner().getUserId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다. (owner만 삭제 가능)");
        }

        teamRepo.delete(team);
    }

    // =========================
    // 멤버 삭제 (owner만)
    // =========================
    @Transactional
    public void removeMember(Long userId, Long collabId, Long targetUserId) {
        CollabTeam team = teamRepo.findById(collabId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀입니다. id=" + collabId));

        if (!team.getOwner().getUserId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다. (owner만 멤버 삭제 가능)");
        }
        if (team.getOwner().getUserId().equals(targetUserId)) {
            throw new IllegalArgumentException("owner는 삭제할 수 없습니다.");
        }

        memberRepo.deleteByCollabIdAndUserId(collabId, targetUserId);
    }

    // =========================
    // 비번 설정/변경 (owner만)
    // =========================
    @Transactional
    public void setPassword(Long userId, Long collabId, CollabRequests.TeamPasswordSetRequest req) {
        CollabTeam team = teamRepo.findById(collabId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀입니다. id=" + collabId));

        if (!team.getOwner().getUserId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다. (owner만 가능)");
        }

        if (Boolean.TRUE.equals(req.isPrivate())) {
            team.setVisibility(CollabEnums.Visibility.private_team);
            if (req.password() == null || req.password().isBlank()) {
                throw new IllegalArgumentException("비밀번호가 필요합니다.");
            }
            team.setPasswordHash(passwordEncoder.encode(req.password()));
        } else {
            team.setVisibility(CollabEnums.Visibility.public_team);
            team.setPasswordHash(null);
        }
    }

    // =========================
    // 비번 인증 (멤버가 되기 전에도 가능)
    // =========================
    @Transactional(readOnly = true)
    public boolean verifyPassword(Long collabId, CollabRequests.TeamPasswordVerifyRequest req) {
        CollabTeam team = teamRepo.findById(collabId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀입니다. id=" + collabId));

        if (team.getVisibility() == CollabEnums.Visibility.public_team) return true;
        if (team.getPasswordHash() == null) return false;

        return passwordEncoder.matches(req.password(), team.getPasswordHash());
    }

    // =========================
    // 원본코드 작성/수정/삭제 (원본은 author만 수정)
    // =========================
    @Transactional
    public Long upsertOriginalCode(Long userId, Long collabId, CollabRequests.CodeUpsertRequest req) {
        if (!memberRepo.existsByCollabIdAndUserId(collabId, userId)) {
            throw new IllegalArgumentException("권한이 없습니다. (팀 멤버만 가능)");
        }

        CollabTeam team = teamRepo.findById(collabId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀입니다. id=" + collabId));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. id=" + userId));

        // 이미 원본 있으면 수정
        var opt = codeRepo.findByTeamCollabId(collabId);
        if (opt.isPresent()) {
            CollabCode code = opt.get();
            if (!code.getAuthor().getUserId().equals(userId)) {
                throw new IllegalArgumentException("권한이 없습니다. (원본 작성자만 수정 가능)");
            }
            code.setLanguage(req.language());
            code.setCode(req.code());
            code.setDescription(req.description());
            code.setVersion(code.getVersion() + 1);
            code.setUpdatedAt(LocalDateTime.now());
            return code.getCodeId();
        }

        CollabCode code = new CollabCode();
        code.setTeam(team);
        code.setAuthor(user);
        code.setLanguage(req.language());
        code.setCode(req.code());
        code.setDescription(req.description());
        code.setVersion(1);
        return codeRepo.save(code).getCodeId();
    }

    @Transactional
    public void deleteOriginalCode(Long userId, Long collabId) {
        CollabCode code = codeRepo.findByTeamCollabId(collabId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 원본 코드입니다."));

        if (!code.getAuthor().getUserId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다. (원본 작성자만 삭제 가능)");
        }
        codeRepo.delete(code);
    }

    // =========================
    // 답글 작성 (버전 자동 증가)
    // =========================
    @Transactional
    public Long createReply(Long userId, Long collabId, CollabRequests.ReplyCreateRequest req) {
        if (!memberRepo.existsByCollabIdAndUserId(collabId, userId)) {
            throw new IllegalArgumentException("권한이 없습니다. (팀 멤버만 가능)");
        }

        CollabTeam team = teamRepo.findById(collabId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀입니다. id=" + collabId));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. id=" + userId));

        // version: 현재 replies 최대값 + 1
        int nextVersion = replyRepo.findByTeamCollabIdOrderByVersionAscCreatedAtAsc(collabId).stream()
                .map(CollabCodeReply::getVersion)
                .max(Integer::compareTo)
                .orElse(0) + 1;

        CollabCodeReply reply = new CollabCodeReply();
        reply.setTeam(team);
        reply.setAuthor(user);
        reply.setMessage(req.message());
        reply.setLanguage(req.language());
        reply.setCode(req.code());
        reply.setVersion(nextVersion);

        return replyRepo.save(reply).getReplyId();
    }

    // =========================
    // 초대 보내기 (owner 또는 멤버 허용은 정책 선택)
    // 지금은 "owner만"으로 깔끔하게.
    // =========================
    @Transactional
    public Long sendInvite(Long userId, Long collabId, CollabRequests.InviteCreateRequest req) {
        CollabTeam team = teamRepo.findById(collabId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀입니다. id=" + collabId));

        if (!team.getOwner().getUserId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다. (owner만 초대 가능)");
        }

        User inviter = team.getOwner();
        User invitee = userRepo.findById(req.inviteeId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. id=" + req.inviteeId()));

        if (memberRepo.existsByCollabIdAndUserId(collabId, invitee.getUserId())) {
            throw new IllegalArgumentException("이미 팀 멤버입니다.");
        }

        if (inviteRepo.findByTeamCollabIdAndInviteeUserIdAndStatus(collabId, invitee.getUserId(), CollabEnums.InviteStatus.pending).isPresent()) {
            throw new IllegalArgumentException("이미 초대를 보냈습니다.");
        }

        CollabInvite invite = new CollabInvite();
        invite.setTeam(team);
        invite.setInviter(inviter);
        invite.setInvitee(invitee);
        invite.setStatus(CollabEnums.InviteStatus.pending);

        return inviteRepo.save(invite).getInviteId();
    }

    @Transactional(readOnly = true)
    public List<CollabResponses.InviteItem> receivedInvites(Long userId) {
        return inviteRepo.findByInviteeUserIdAndStatusOrderByCreatedAtDesc(userId, CollabEnums.InviteStatus.pending).stream()
                .map(i -> new CollabResponses.InviteItem(
                        i.getInviteId(),
                        i.getTeam().getCollabId(),
                        i.getTeam().getTitle(),
                        i.getInviter().getUserId(),
                        i.getInviter().getNickname(),
                        i.getInvitee().getUserId(),
                        i.getInvitee().getNickname(),
                        i.getStatus().name(),
                        i.getCreatedAt()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CollabResponses.InviteItem> sentInvites(Long userId) {
        return inviteRepo.findByInviterUserIdOrderByCreatedAtDesc(userId).stream()
                .map(i -> new CollabResponses.InviteItem(
                        i.getInviteId(),
                        i.getTeam().getCollabId(),
                        i.getTeam().getTitle(),
                        i.getInviter().getUserId(),
                        i.getInviter().getNickname(),
                        i.getInvitee().getUserId(),
                        i.getInvitee().getNickname(),
                        i.getStatus().name(),
                        i.getCreatedAt()
                ))
                .toList();
    }

    @Transactional
    public void acceptInvite(Long userId, Long inviteId) {
        CollabInvite invite = inviteRepo.findById(inviteId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 초대입니다. id=" + inviteId));

        if (!invite.getInvitee().getUserId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다. (본인에게 온 초대만 수락 가능)");
        }
        if (invite.getStatus() != CollabEnums.InviteStatus.pending) {
            throw new IllegalArgumentException("이미 처리된 초대입니다.");
        }

        invite.setStatus(CollabEnums.InviteStatus.accepted);

        // 멤버 추가
        CollabMember m = new CollabMember();
        m.setCollabId(invite.getTeam().getCollabId());
        m.setUserId(userId);
        m.setRole(CollabEnums.MemberRole.member);
        memberRepo.save(m);
    }

    @Transactional
    public void declineInvite(Long userId, Long inviteId) {
        CollabInvite invite = inviteRepo.findById(inviteId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 초대입니다. id=" + inviteId));

        if (!invite.getInvitee().getUserId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다. (본인에게 온 초대만 거절 가능)");
        }
        if (invite.getStatus() != CollabEnums.InviteStatus.pending) {
            throw new IllegalArgumentException("이미 처리된 초대입니다.");
        }

        invite.setStatus(CollabEnums.InviteStatus.declined);
    }

    @Transactional
    public void cancelInvite(Long userId, Long inviteId) {
        CollabInvite invite = inviteRepo.findById(inviteId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 초대입니다. id=" + inviteId));

        if (!invite.getInviter().getUserId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다. (초대한 사람만 취소 가능)");
        }
        if (invite.getStatus() != CollabEnums.InviteStatus.pending) {
            throw new IllegalArgumentException("이미 처리된 초대입니다.");
        }

        invite.setStatus(CollabEnums.InviteStatus.canceled);
    }
}
