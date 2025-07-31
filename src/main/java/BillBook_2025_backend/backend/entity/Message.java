package BillBook_2025_backend.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String messageText;

    private LocalDateTime sendAt;

    private boolean isRead = false;

    @ManyToOne
    private ChatRoom chatRoom;

    @ManyToOne
    private User user;
}
