package BillBook_2025_backend.backend.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikeBook {
    private Long id;
    private Long bookId;
    private String userId;

    public LikeBook(Long bookId, String userId) {
        this.bookId = bookId;
        this.userId = userId;
    }


}
