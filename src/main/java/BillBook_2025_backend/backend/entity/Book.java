package BillBook_2025_backend.backend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Book {

    private Long id;
    private String userId;  //빌려준 사람
    private String borrowId;  //빌린 사람
    private Long bookPoint;
    private String bookPic;  //자료형 나중에 체크
    private LocalDateTime time;
    private String location; //자료형 나중에 체크
    private String content;
    private String status;
    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private String category;
    private String description;
    private Long total;
    private LocalDateTime returnTime;

}
