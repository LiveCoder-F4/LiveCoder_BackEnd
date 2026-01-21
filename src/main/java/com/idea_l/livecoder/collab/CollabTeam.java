package com.idea_l.livecoder.collab;

import com.idea_l.livecoder.user.User;
import com.idea_l.livecoder.problem.Problem;
import com.idea_l.livecoder.common.CollabVisibility;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "collab_teams")
public class CollabTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collab_id")
    private Long collabId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private CollabVisibility visibility = CollabVisibility.PRIVATE;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "collabTeam", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CollabMember> members;

    @OneToOne(mappedBy = "collabTeam", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CollabCode code;

    @OneToMany(mappedBy = "collabTeam", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CollabCodeReply> replies;

    @OneToMany(mappedBy = "collabTeam", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CollabInvite> invites;

}
