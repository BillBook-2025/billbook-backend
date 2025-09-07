package BillBook_2025_backend.backend.dto;

import BillBook_2025_backend.backend.entity.Book;
import BillBook_2025_backend.backend.entity.BookStatus;
import BillBook_2025_backend.backend.entity.Picture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
@Builder
@Getter
@Setter
public class BookDto {
    private Long bookId;
    private LocalDateTime time;
    private Long sellerId;
    private Long bookPoint;
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
    private List<PictureDto> bookPic = new ArrayList<>();

    public BookDto(Book book) {
        this.bookId = book.getId();
        this.sellerId = book.getSeller().getId();
        this.bookPoint = book.getBookPoint();
        this.location = book.getLocation();
        this.content = book.getContent();
        this.status = book.getStatus();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.publisher = book.getPublisher();
        this.isbn = book.getIsbn();
        this.category = book.getCategory();
        this.description = book.getDescription();
        this.time = book.getTime();
        if (book.getPicture() != null) {
            for (Picture picture : book.getPicture()) {
                PictureDto dto = new PictureDto(picture.getUrl(), picture.getFilename());
                this.bookPic.add(dto);
            }

        }

    }
}
