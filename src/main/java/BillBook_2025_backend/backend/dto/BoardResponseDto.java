package BillBook_2025_backend.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import BillBook_2025_backend.backend.entity.Board;
import BillBook_2025_backend.backend.entity.Picture;

@Getter @Setter @NoArgsConstructor
public class BoardResponseDto {
    private Long boardId;
    private String title;
    private String category;
    private String isbn; // 책 정보
    // private String boardPic;  //자료형 나중에 체크
    private List<PictureDto> boardPic = new ArrayList<>();;  //자료형 나중에 체크
    private String content;
    private String userId;
    private LocalDateTime createdAt;
    private long likeCount;
    private long commentsCount;

    public static BoardResponseDto fromEntity(Board board, long likeCount, long commentsCount) {
        BoardResponseDto dto = new BoardResponseDto();
        dto.setBoardId(board.getBoardId());
        dto.setTitle(board.getTitle());
        dto.setCategory(board.getCategory());
        dto.setIsbn(board.getIsbn());
        dto.setContent(board.getContent());
        dto.setUserId(board.getUserId());
        dto.setCreatedAt(board.getCreatedAt());
        dto.setLikeCount(likeCount);
        dto.setCommentsCount(commentsCount);

        if (board.getPicture() != null) {
            for (Picture picture : board.getPicture()) {
                PictureDto picDto = new PictureDto(picture.getUrl(), picture.getFilename());
                dto.getBoardPic().add(picDto);
            }
        }

        return dto;
    }
}