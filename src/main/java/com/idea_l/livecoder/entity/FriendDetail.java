package com.idea_l.livecoder.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "friend_detail",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "friend_id"}))
public class FriendDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend;

    @Column(name = "is_blocked")
    private Boolean isBlocked = false;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

    @Column(name = "battles")
    private Integer battles = 0;

    @Column(name = "wins")
    private Integer wins = 0;

    @Column(name = "losses")
    private Integer losses = 0;

    @Column(name = "draws")
    private Integer draws = 0;

    @Column(name = "win_rate", precision = 5, scale = 2, insertable = false, updatable = false)
    private BigDecimal winRate;

}