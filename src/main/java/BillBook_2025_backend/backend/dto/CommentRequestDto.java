package BillBook_2025_backend.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class CommentRequestDto {
    // private Long boardId;     // 게시글 ID
    private String content;   // 댓글 내용
    // private Long replyToId;   // 부모 댓글 ID (답글일 경우)
}