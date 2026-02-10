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
@Table(name = "collab_codes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"collab_id"})) // 팀당 원본 1개
public class CollabCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code_id")
    private Long codeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collab_id", nullable = false)
    private CollabTeam team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "language", nullable = false)
    private String language; // 'C++','JAVA','python' 등 (ENUM이면 String으로 받는 게 편함)

    @Column(name = "code", nullable = false, columnDefinition = "TEXT")
    private String code;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "version", nullable = false)
    private Integer version = 1;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
