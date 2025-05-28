package BillBook_2025_backend.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookPostRequestDto {

    private String title;
    private String author;
    private String publisher;
    private String isbn;
    //private String category;
    private String description;
    //private Long total;

    private String userId;  //빌려준 사람
    private Long bookPoint;
    private String bookPic;  //자료형 나중에 체크
    private String location; //자료형 나중에 체크
    private String content;
}
