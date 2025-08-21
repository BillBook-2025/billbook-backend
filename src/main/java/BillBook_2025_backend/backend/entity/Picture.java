package BillBook_2025_backend.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
public class Picture {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String filename;
    private String url;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "chatRoom_id")
    private ChatRoom chatRoom;

    public Picture() {};

    public Picture(String filename, String url, Book book) {
        this.filename = filename;
        this.url = url;
        this.book = book;
    }

    public Picture(String filename, String url, Member member) {
        this.filename = filename;
        this.url = url;
        this.member = member;
    }

}
