package BillBook_2025_backend.backend.dto;

import BillBook_2025_backend.backend.entity.Message;
import BillBook_2025_backend.backend.entity.MessageType;
import lombok.*;

import java.time.LocalDateTime;
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private Long chatRoomId;
    private Long senderId;
    private String message;
    private MessageType type;
    private LocalDateTime sendAt;

    public MessageDto(Message message) {
        this.chatRoomId = message.getChatRoom().getId();
        this.senderId = message.getSender().getId();
        this.message = message.getMessage();
        this.type = message.getType();
        this.sendAt = message.getSendAt();
    }
}
