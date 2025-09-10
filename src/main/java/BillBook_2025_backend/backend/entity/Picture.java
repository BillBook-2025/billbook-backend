package BillBook_2025_backend.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Data
@Entity
@AllArgsConstructor
public class Picture {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String filename;
    private String url;

    @OneToOne
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;

    @ManyToOne
    @JoinColumn(name = "book_id")
    @JsonIgnore
    private Book book;

    @ManyToOne
    @JoinColumn(name = "chatRoom_id")
    @JsonIgnore
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

    public Picture(String filename, String url, ChatRoom chatRoom, Member member) {
        this.filename = filename;
        this.url = url;
        this.chatRoom = chatRoom;
        this.member = member;
    }

}
