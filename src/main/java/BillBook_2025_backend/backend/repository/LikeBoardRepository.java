package BillBook_2025_backend.backend.repository;

import BillBook_2025_backend.backend.entity.Board;
import BillBook_2025_backend.backend.entity.LikeBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeBoardRepository extends JpaRepository<LikeBoard, Long> {

    List<LikeBoard> findByMemberId(Long memberId);

    // Board.boardId + Member.id
    Optional<LikeBoard> findByBoardBoardIdAndMemberId(Long boardId, Long memberId);

    List<LikeBoard> findByBoardBoardId(Long boardId);

    @Query("select count(l.id) from LikeBoard l where l.board.boardId = :boardId")
    Long countByBoardId(@Param("boardId") Long boardId);
}
