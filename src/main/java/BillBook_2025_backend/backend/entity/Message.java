package BillBook_2025_backend.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String message;

    private LocalDateTime sendAt;
    private LocalDateTime readAt;

    @Enumerated(EnumType.STRING)
    private MessageType type;



    @ManyToOne
    private ChatRoom chatRoom;

    @ManyToOne
    private Member sender;

    public void setEnterMessage() {
        this.message = "[ " + MessageType.ENTER + " ] " + this.sender;
    }

    public void setExitMessage() {
        this.message = "[ " + MessageType.LEAVE + " ] " + this.sender;
    }

    public Message (String message, MessageType type, ChatRoom chatRoom, Member sender) {
        this.message = message;
        this.type = type;
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.sendAt = LocalDateTime.now();
    }
}
