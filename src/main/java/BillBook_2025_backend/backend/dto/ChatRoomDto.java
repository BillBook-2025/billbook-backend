package BillBook_2025_backend.backend.dto;

import BillBook_2025_backend.backend.entity.Book;
import BillBook_2025_backend.backend.entity.ChatRoom;
import BillBook_2025_backend.backend.entity.Member;
import BillBook_2025_backend.backend.entity.Message;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ChatRoomDto {

    private Long id;

    private LocalDateTime createdAt;

    private String name;

    private Long buyerId;

    private Long sellerId;


    private Long bookId;

    private List<MessageDto> messages = new ArrayList<>();

    private String lastMessage;

    public ChatRoomDto() {}

    public ChatRoomDto(ChatRoom chatRoom) {
        this.id = chatRoom.getId();
        this.createdAt = chatRoom.getCreatedAt();
        this.name = chatRoom.getName();
        this.buyerId = chatRoom.getBuyer().getId();
        this.sellerId = chatRoom.getSeller().getId();
        this.bookId = chatRoom.getBook().getId();
        for (Message message : chatRoom.getMessages()) {
            this.messages.add(new MessageDto(message));
        }
    }

    public ChatRoomDto(ChatRoom chatRoom, String lastMessage) {
        this.id = chatRoom.getId();
        this.createdAt = chatRoom.getCreatedAt();
        this.name = chatRoom.getName();
        this.buyerId = chatRoom.getBuyer().getId();
        this.sellerId = chatRoom.getSeller().getId();
        this.bookId = chatRoom.getBook().getId();
        for (Message message : chatRoom.getMessages()) {
            this.messages.add(new MessageDto(message));
        }
        this.lastMessage = lastMessage;
    }
}
