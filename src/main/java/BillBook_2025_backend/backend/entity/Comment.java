package BillBook_2025_backend.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    // 하.. 자기 자신을 참조하는 필드는 꼭 관계어노테이션 붙혀줘야함;;;
    /** 게시글 (N:1) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    /** 부모 댓글 (대댓글 기능용, N:1) */
    @ManyToOne(fetch = FetchType.LAZY) // 부모 댓글(replyTo) 하나만 가질 수 있다는 의미!
    @JoinColumn(name = "reply_to")
    private Comment replyTo;

    /** 자식 댓글 목록 (1:N) */
    @OneToMany(mappedBy = "replyTo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> replies = new ArrayList<>();

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    private Long userId;
    private LocalDateTime createdAt;

    private Boolean deleted = false; // 소프트 삭제용 필드
}