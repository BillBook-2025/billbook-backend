package BillBook_2025_backend.backend.entity;

import jakarta.persistence.*;

@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String filename;
    private String url;

    @ManyToOne
    private Member member;

    @ManyToOne
    private Book book;

    @ManyToOne
    private ChatRoom chatRoom;



}
