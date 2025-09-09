package BillBook_2025_backend.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import BillBook_2025_backend.backend.entity.Comment;

@Getter @Setter @NoArgsConstructor
public class CommentResponseDto {
    private Long commentId;
    private Long boardId;
    private Long replyTo;
    private String content;
    private Long userId;
    private LocalDateTime createdAt;

    public static CommentResponseDto fromEntity(BillBook_2025_backend.backend.entity.Comment comment) {
        CommentResponseDto dto = new CommentResponseDto();
        dto.setCommentId(comment.getCommentId());
        if (comment.getBoard() != null) {
            dto.setBoardId(comment.getBoard().getBoardId());
        }
        if (comment.getReplyTo() != null) {
          dto.setReplyTo(comment.getReplyTo().getCommentId());
        }
        dto.setContent(comment.getContent());
        dto.setUserId(comment.getUserId());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}