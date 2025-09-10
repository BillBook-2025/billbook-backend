package BillBook_2025_backend.backend.dto;

import BillBook_2025_backend.backend.entity.Book;
import BillBook_2025_backend.backend.entity.Picture;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    private Long sellerId;  //빌려준 사람
    private Long bookPoint;
    private List<PictureDto> bookPic = new ArrayList<>();  //자료형 나중에 체크
    private String location; //자료형 나중에 체크
    private String content;

    public BookPostRequestDto(Book book) {
        this.bookPoint = book.getBookPoint();
        if (book.getPicture() != null) {
            for (Picture picture : book.getPicture()) {
                PictureDto dto = new PictureDto(picture.getUrl(), picture.getFilename());
                this.bookPic.add(dto);
            }

        }
        this.sellerId = book.getSeller().getId();
        this.location = book.getLocation();
        this.content = book.getContent();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.publisher = book.getPublisher();
        this.isbn = book.getIsbn();
        this.description = book.getDescription();
    }
}
