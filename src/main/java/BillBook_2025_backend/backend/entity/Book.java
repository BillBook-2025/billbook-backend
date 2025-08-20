package BillBook_2025_backend.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long sellerId;  //빌려준 사람
    private Long buyerId;  //빌린 사람
    private Long bookPoint;
    private LocalDateTime time;
    private String location; //자료형 나중에 체크
    private String content;
    //책상태 양호한지 그런 상태 나타내는 변수
    private BookStatus status;
    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private String category;
    private String description;
    private Long total;
    private LocalDateTime returnTime;


    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Picture> picture = new ArrayList<>();

}
