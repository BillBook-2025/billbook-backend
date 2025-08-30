package BillBook_2025_backend.backend.handler;

import BillBook_2025_backend.backend.dto.MessageDto;
import BillBook_2025_backend.backend.entity.ChatRoom;
import BillBook_2025_backend.backend.entity.Member;
import BillBook_2025_backend.backend.entity.Message;
import BillBook_2025_backend.backend.entity.MessageType;
import BillBook_2025_backend.backend.repository.ChatRoomRepository;
import BillBook_2025_backend.backend.repository.MemberRepository;
import BillBook_2025_backend.backend.repository.MessageRepository;
import BillBook_2025_backend.backend.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private ObjectMapper objectMapper;

    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final ChatService chatService;
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public ChatWebSocketHandler(MemberRepository memberRepository, ChatService chatService, ChatRoomRepository chatRoomRepository, MessageRepository messageRepository) {
        this.memberRepository = memberRepository;
        this.chatService = chatService;
        this.chatRoomRepository = chatRoomRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("[afterConnectionEstablished] " + session.getId());
        String sessionId = session.getId();
        sessions.put(sessionId, session); //세션에 저장

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        MessageDto messageDto = objectMapper.readValue(textMessage.getPayload(), MessageDto.class);
        ChatRoom chatRoom = chatRoomRepository.findById(messageDto.getChatRoomId())
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found")); //채팅방 확인
        Member sender = memberRepository.findById(messageDto.getSenderId())
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));

        Long memberId = (Long) session.getAttributes().get("memberId");
        if (!memberId.equals(messageDto.getSenderId())) {
            throw new IllegalStateException("Sender ID 불일치");  // 사용자 검증
        }

        Message message = Message.builder()
                .message(messageDto.getMessage())
                .type(messageDto.getType())
                .chatRoom(chatRoom)
                .sender(sender)
                .sendAt(LocalDateTime.now())
                .build();


        if (chatRoom == null) {
            throw new EntityNotFoundException("Chat room not found");
        } else {
            if (message.getType().equals(MessageType.ENTER)) {
                chatRoom.addSession(session);
                message.setEnterMessage();
            } else if (message.getType().equals(MessageType.CHAT)) {
                messageRepository.save(message);
                MessageDto messageResponse = new MessageDto(message);
                chatRoom.getSessions().parallelStream().forEach(s -> {
                    if (!s.getId().equals(session.getId())) {
                        chatService.sendMessage(s, messageResponse);
                    }
                });
            } else if (message.getType().equals(MessageType.LEAVE)) {
                message.setExitMessage();
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("[연결 완료] " + session.getId());
        var sessionId = session.getId();

        sessions.remove(sessionId);
        chatRoomRepository.findAll().forEach(r -> r.removeSession(session));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }

}
