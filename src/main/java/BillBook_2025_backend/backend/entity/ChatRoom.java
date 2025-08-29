package BillBook_2025_backend.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime createdAt;

    @ManyToOne
    private Member buyer;

    @ManyToOne
    private Member seller;

    @ManyToOne
    private Book book;

    @OneToMany
    private List<Message> messages;
}
