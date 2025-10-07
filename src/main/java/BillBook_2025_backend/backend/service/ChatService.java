package BillBook_2025_backend.backend.service;

import BillBook_2025_backend.backend.dto.BookResponse;
import BillBook_2025_backend.backend.dto.ChatRoomDto;
import BillBook_2025_backend.backend.dto.MessageDto;
import BillBook_2025_backend.backend.dto.PictureDto;
import BillBook_2025_backend.backend.entity.*;
import BillBook_2025_backend.backend.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ObjectMapper objectMapper;

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    ChatRoomRepository chatRoomRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    PictureRepository pictureRepository;





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

//    public void sendMessage(WebSocketSession reciever, MessageDto message) {
//        try {
//            if (reciever != null && reciever.isOpen()) {
//                reciever.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
//
//            }
//        } catch (IOException e) {
//            log.error(e.getMessage(), e);
//        }
//    }

    @Transactional
    public MessageDto saveAndBuild(Long chatRoomId, Long userId, MessageDto messageDto) {
        ChatRoom room = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new EntityNotFoundException("not found chatroom"));
        Member member = memberRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("not found member"));

        Message msg = Message.builder()
                .chatRoom(room)
                .sender(member)
                .type(messageDto.getType())
                .message(messageDto.getMessage())
                .sendAt(LocalDateTime.now())
                .build();

        messageRepository.save(msg);

        return MessageDto.builder()
                .chatRoomId(msg.getChatRoom().getId())
                .senderId(msg.getSender().getId())
                .type(msg.getType())
                .message(msg.getMessage())
                .sendAt(msg.getSendAt())
                .build();
    }

    public List<ChatRoomDto> getAllChatRooms(Long userId) {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("not found member"));
        List<ChatRoomDto> responses = new ArrayList<>();
        for (ChatRoom chatRoom : chatRoomRepository.findByMember(member)) {
            Optional<Message> lastMessageOpt = messageRepository.findFirstByChatRoomIdOrderBySendAtDesc(chatRoom.getId());
            String lastMessage = lastMessageOpt.map(Message::getMessage).orElse("");
            responses.add(new ChatRoomDto(chatRoom, lastMessage));
        }
        return responses;

    }

    public void deleteChatRoom(Long chatRoomId) {
        chatRoomRepository.deleteById(chatRoomId);
    }

    public ChatRoomDto getChatRoom(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new EntityNotFoundException("not found chat room"));
        return new ChatRoomDto(chatRoom);
    }


    public ChatRoom openChatRoom(Long bookId, Long buyerId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("not found book"));
        Member buyer = memberRepository.findById(buyerId).orElseThrow(() -> new EntityNotFoundException("not found member"));
        ChatRoom chatRoom = chatRoomRepository.findByBookAndBuyer(book, buyer)
                .orElseGet(() -> chatRoomRepository.save(ChatRoom.builder()
                        .book(book)
                        .buyer(buyer)
                        .seller(book.getSeller())
                        .createdAt(LocalDateTime.now())
                        .build()));
        return chatRoom;

    }

    public Page<MessageDto> getMessageHistory(Long chatRoomId, int page, int size) {
        Page<Message> messages = messageRepository.findPageByChatRoomId(chatRoomId, PageRequest.of(page, size));
        Page<MessageDto> responses = messages.map(MessageDto::new);

        return responses;


    }

    public BookResponse getDeadLine(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new EntityNotFoundException("not found chat room"));
        Book book = chatRoom.getBook();
        LocalDateTime returnTime = book.getReturnTime();
        return BookResponse.builder()
                .returnTime(returnTime)
                .build();
    }

    public void setDeadLine(Long chatRoomId, BookResponse deadLine) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new EntityNotFoundException("not found chat room"));
        Book book = chatRoom.getBook();
        LocalDateTime returnTime = deadLine.getReturnTime();
        book.setReturnTime(returnTime);
    }

    public PictureDto sendPicture(PictureDto pictureDto, Long chatRoomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new EntityNotFoundException("not found chat room"));
        Member member = memberRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("not found member"));
        if (!chatRoom.getSeller().equals(member) && !chatRoom.getBuyer().equals(member)) {
            throw new IllegalArgumentException("잘못된 사용자의 접근");
        }
        Picture picture = Picture.builder()
                .filename(pictureDto.getFilename())
                .url(pictureDto.getUrl())
                .member(member)
                .chatRoom(chatRoom)
                .build();
        pictureRepository.save(picture);
        return pictureDto;

    }
}
