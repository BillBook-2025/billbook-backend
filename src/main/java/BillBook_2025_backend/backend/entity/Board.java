package BillBook_2025_backend.backend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(name = "boards")
public class Board {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;
    private String title;
    private String category; // "리뷰" | "나눔" | "기타"
    private String isbn;
    private String userId; // 글 쓴 사람
    private LocalDateTime createdAt; // 언제 작성
    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Picture> picture = new ArrayList<>();
}