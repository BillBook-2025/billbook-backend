package BillBook_2025_backend.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime createdAt;

    private String name;

    @ManyToOne
    private Member buyer;

    @ManyToOne
    private Member seller;

    @ManyToOne
    private Book book;

    @OneToMany
    private List<Message> messages;

    @Builder
    public ChatRoom(String name, Member buyer, Member seller, Book book) {
        this.name = name;
        this.buyer = buyer;
        this.seller = seller;
        this.book = book;
        this.createdAt = LocalDateTime.now();
    }

    public boolean hasParticipants(Long userId) {
        return (buyer != null && buyer.getId().equals(userId)) || (seller != null && seller.getId().equals(userId));
    }

    @Transient
    private Set<WebSocketSession> sessions = new HashSet<>();

    public void addSession(WebSocketSession session) {
        sessions.add(session);
    }

    public void removeSession(WebSocketSession session) {
        sessions.remove(session);
    }
}
