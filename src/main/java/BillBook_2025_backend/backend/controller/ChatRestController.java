package BillBook_2025_backend.backend.controller;

import BillBook_2025_backend.backend.dto.BookResponse;
import BillBook_2025_backend.backend.dto.ChatRoomDto;
import BillBook_2025_backend.backend.dto.MessageDto;
import BillBook_2025_backend.backend.dto.PictureDto;
import BillBook_2025_backend.backend.entity.ChatRoom;
import BillBook_2025_backend.backend.service.ChatService;
import BillBook_2025_backend.backend.service.S3UploadService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatRestController {
    private final ChatService chatService;
    private final S3UploadService s3UploadService;

    @PostMapping("/api/books/{bookId}/chatRoom")  //보안??  //채팅방 열기 및 생성
    public ResponseEntity<ChatRoomDto> openChatRoom(@PathVariable Long bookId, @RequestParam Long buyerId) {
        ChatRoom chatRoom = chatService.openChatRoom(bookId, buyerId);
        return ResponseEntity.ok(new ChatRoomDto(chatRoom));
    }

    @GetMapping("/api/chatRooms/{chatRoomId}/messages") //대화 내용 불러오기
    public ResponseEntity<Page<MessageDto>> history(@PathVariable Long chatRoomId,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "30") int size) {
        Page<MessageDto> messageHistory = chatService.getMessageHistory(chatRoomId, page, size);
        return ResponseEntity.ok(messageHistory);
    }

    @DeleteMapping("/api/chatRooms/{chatRoomId}")
    public ResponseEntity<String> deleteChatRoom(@PathVariable Long chatRoomId) {
        chatService.deleteChatRoom(chatRoomId);
        return ResponseEntity.ok("Deleted chat room");
    }

    @GetMapping("/api/users/{userId}/chatRooms")
    public ResponseEntity<List<ChatRoomDto>> getChatRooms(@PathVariable Long userId) {
        List<ChatRoomDto> allChatRooms = chatService.getAllChatRooms(userId);
        return ResponseEntity.ok(allChatRooms);
    }

    @GetMapping("/api/chatRooms/{chatRoomId}/deadline")
    public ResponseEntity<BookResponse> getDeadLine(@PathVariable Long chatRoomId) {
        BookResponse deadLine = chatService.getDeadLine(chatRoomId);
        return ResponseEntity.ok(deadLine);
    }

    @PostMapping("/api/chatRooms/{chatRoomId}/deadline")
    public ResponseEntity<String> setDeadLine(@PathVariable Long chatRoomId, BookResponse deadLine) {
        chatService.setDeadLine(chatRoomId, deadLine);
        return ResponseEntity.ok("반납 기한 설정 완료");
    }

    @PostMapping("/api/chatRooms/{chatRoomId}/picture")
    public ResponseEntity<PictureDto> sendPicture(@PathVariable Long chatRoomId, @RequestParam MultipartFile file, HttpSession session) throws IOException {
        PictureDto pictureDto = s3UploadService.saveFile(file);
        Long userId = (Long) session.getAttribute("id");
        PictureDto pictureResponse = chatService.sendPicture(pictureDto, chatRoomId, userId);
        return ResponseEntity.ok(pictureResponse);
    }


}
