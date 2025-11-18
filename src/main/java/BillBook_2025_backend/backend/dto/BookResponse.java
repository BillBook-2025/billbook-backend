package BillBook_2025_backend.backend.dto;

import BillBook_2025_backend.backend.entity.Book;
import BillBook_2025_backend.backend.entity.BookCondition;
import BillBook_2025_backend.backend.entity.BookStatus;
import BillBook_2025_backend.backend.entity.Picture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Builder
@Getter
@Setter
@AllArgsConstructor
public class BookResponse {

    private Long bookId;
    //빌려준 사람 닉네임은 표시해야할까?
    private Long bookPoint;
    private List<PictureDto> bookPic = new ArrayList<>();;  //자료형 나중에 체크
    private LocalDateTime time;
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
    private LocalDateTime returnTime;
    private Long sellerId;
    private BookCondition cond;
    private Long buyerId;

    private LocationDto locate;


    public BookResponse(LocalDateTime returnTime) {
        this.returnTime = returnTime;
    }


    public BookResponse(Book book) {
        this.bookId = book.getId();
        this.bookPoint = book.getBookPoint();
        if (book.getPicture() != null) {
            for (Picture picture : book.getPicture()) {
                PictureDto dto = new PictureDto(picture.getUrl(), picture.getFilename());
                this.bookPic.add(dto);
            }

        }
        this.sellerId = book.getSeller().getId();
        if(book.getBuyer() != null) {
            this.buyerId = book.getBuyer().getId();
        }
        this.time = book.getTime();
        this.content = book.getContent();
        this.status = book.getStatus();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.publisher = book.getPublisher();
        this.isbn = book.getIsbn();
        this.category = book.getCategory();
        this.description = book.getDescription();
        this.total = book.getTotal();
        this.cond = book.getCond();
        LocationDto locateDto = new LocationDto(book.getAddress(), book.getLatitude(), book.getLongitude(), book.getRegionLevel1(), book.getRegionLevel2(), book.getRegionLevel3());
        this.locate = locateDto;
    }




}
