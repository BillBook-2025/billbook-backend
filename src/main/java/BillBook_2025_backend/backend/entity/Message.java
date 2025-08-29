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

    @Enumerated(EnumType.STRING)
    private MessageType type;

    private boolean isRead = false;

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
}
