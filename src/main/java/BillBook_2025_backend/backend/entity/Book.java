package BillBook_2025_backend.backend.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Book {

    private Long id;
    private String user_id;
    private String borrow_id;
    private Long bookpoint;
    private String book_pic;  //자료형 나중에 체크
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
    private LocalDateTime returntime;
}
