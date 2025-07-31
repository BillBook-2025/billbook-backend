package BillBook_2025_backend.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;  //빌려준 사람
    private Long borrowId;  //빌린 사람
    private Long bookPoint;
    private String bookPic;  //자료형 나중에 체크
    private LocalDateTime time;
    private String location; //자료형 나중에 체크
    private String content;
    private BookStatus status;
    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private String category;
    private String description;
    private Long total;
    private LocalDateTime returnTime;

}
