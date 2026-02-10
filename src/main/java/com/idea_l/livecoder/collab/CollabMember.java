package com.idea_l.livecoder.collab;

import com.idea_l.livecoder.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "collab_members")
@IdClass(CollabMemberId.class)
public class CollabMember {

    @Id
    @Column(name = "collab_id")
    private Long collabId;

    @Id
    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collab_id", insertable = false, updatable = false)
    private CollabTeam team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private CollabEnums.MemberRole role = CollabEnums.MemberRole.member;

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;
}
