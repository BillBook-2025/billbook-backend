package BillBook_2025_backend.backend.repository;

import BillBook_2025_backend.backend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoard_BoardId(Long boardId);
    // 특정 댓글의 대댓 목록
    List<Comment> findByReplyTo_CommentId(Long commentId);
}