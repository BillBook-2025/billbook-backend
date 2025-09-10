package BillBook_2025_backend.backend.controller;

import BillBook_2025_backend.backend.dto.MessageDto;
import BillBook_2025_backend.backend.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatStompController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chatroom/{chatroomId}/chat")
    public void send(@DestinationVariable Long chatroomId, MessageDto messageDto) {

        Long senderId = messageDto.getSenderId();
        MessageDto saved = chatService.saveAndBuild(chatroomId, senderId, messageDto);
        messagingTemplate.convertAndSend("/topic/chatroom/" + chatroomId + "/chat", saved);
        //messagingTemplate.convertAndSend("/chatroom/" + chatroomId + "/chat", saved);
    }
}
