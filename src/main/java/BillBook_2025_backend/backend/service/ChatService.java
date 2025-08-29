package BillBook_2025_backend.backend.service;

import BillBook_2025_backend.backend.dto.MessageDto;
import BillBook_2025_backend.backend.entity.Book;
import BillBook_2025_backend.backend.entity.ChatRoom;
import BillBook_2025_backend.backend.entity.Member;
import BillBook_2025_backend.backend.entity.Message;
import BillBook_2025_backend.backend.repository.BookRepository;
import BillBook_2025_backend.backend.repository.ChatRoomRepository;
import BillBook_2025_backend.backend.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ObjectMapper objectMapper;

    @Autowired
    MemberRepository memberRepository;
    BookRepository bookRepository;
    ChatRoomRepository chatRoomRepository;



    public ChatRoom createChatRoom(String roomName, Long buyerId, Long bookId) {
        Member buyer = memberRepository.findById(buyerId).orElseThrow(() -> new EntityNotFoundException("해당 구매자를 찾을 수 없습니다."));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("해당 거래글을 찾을 수 없습니다."));
        Member seller = book.getSeller();
        ChatRoom chatRoom = ChatRoom.builder()
                .name(roomName)
                .seller(seller)
                .buyer(buyer)
                .book(book)
                .createdAt(LocalDateTime.now())
                .build();
        return chatRoomRepository.save(chatRoom);
    }

    public void sendMessage(WebSocketSession reciever, MessageDto message) {
        try {
            if (reciever != null && reciever.isOpen()) {
                reciever.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));

            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }



}
