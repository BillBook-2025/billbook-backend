package BillBook_2025_backend.backend.dto;

import BillBook_2025_backend.backend.entity.Book;
import BillBook_2025_backend.backend.entity.BookStatus;
import BillBook_2025_backend.backend.entity.Picture;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BookResponse {

    private Long bookId;
    //빌려준 사람 닉네임은 표시해야할까?
    private Long bookPoint;
    private List<PictureDto> bookPic = new ArrayList<>();;  //자료형 나중에 체크
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
    private Long total;  //이게 뭐였지 좋아요수??였나


    public BookResponse(Book book) {
        this.bookId = book.getId();
        this.bookPoint = book.getBookPoint();
        if (book.getPicture() != null) {
            for (Picture picture : book.getPicture()) {
                PictureDto dto = new PictureDto(picture.getUrl(), picture.getFilename());
                this.bookPic.add(dto);
            }

        }
        this.time = book.getTime();
        this.location = book.getLocation();
        this.content = book.getContent();
        this.status = book.getStatus();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.publisher = book.getPublisher();
        this.isbn = book.getIsbn();
        this.category = book.getCategory();
        this.description = book.getDescription();
        this.total = book.getTotal();
    }




}
