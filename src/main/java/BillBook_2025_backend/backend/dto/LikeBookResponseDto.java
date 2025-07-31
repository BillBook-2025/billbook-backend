package BillBook_2025_backend.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeBookResponseDto {
    private Long bookId;
    private Long likeCount;
}
