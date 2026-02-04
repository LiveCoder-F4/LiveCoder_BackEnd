package com.idea_l.livecoder.home;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "news")
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id")
    private Long newsId;

    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @Column(name = "url", nullable = false, length = 500)
    private String url;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    //뉴스 null 방지
    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private LocalDateTime createdAt;


}
