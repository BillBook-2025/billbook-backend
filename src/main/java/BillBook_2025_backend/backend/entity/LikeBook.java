package BillBook_2025_backend.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.catalina.User;

@Entity
@Getter
@Setter
public class LikeBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Book book;

    @ManyToOne
    private Member member;

    protected LikeBook() {}

    public LikeBook(Book book, Member member) {
        this.book = book;
        this.member = member;
    }

}
