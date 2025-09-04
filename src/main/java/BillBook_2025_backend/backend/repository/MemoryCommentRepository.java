package BillBook_2025_backend.backend.repository;

import BillBook_2025_backend.backend.entity.Comment;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MemoryCommentRepository {
    private final Map<Long, Comment> store = new ConcurrentHashMap<>();
    private long sequence = 0L;
    
    public Comment save(Comment comment) {
        if (comment.getCommentId() == null) {
            comment.setCommentId(++sequence);
            comment.setCreatedAt(LocalDateTime.now());
        }

        if (comment.getReplyTo() != null) {
            Comment parentComment = store.get(comment.getReplyTo().getCommentId());
            if (parentComment != null) {
                comment.setReplyTo(parentComment);
            }
        }

        store.put(comment.getCommentId(), comment);
        return comment;
    }

    public List<Comment> findByBoardId(Long boardId) {
        List<Comment> result = new ArrayList<>();
        for (Comment c : store.values()) {
            if (c.getBoard().getBoardId().equals(boardId)) result.add(c);
        }
        return result;
    }

    public Optional<Comment> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public void delete(Long id) {
        store.remove(id);
    }

    public List<Comment> findReplies(Long commentId) {
        List<Comment> replies = new ArrayList<>();
        for (Comment c : store.values()) {
            if (c.getReplyTo() != null && c.getReplyTo().getCommentId().equals(commentId)) {
                replies.add(c);
            }
        }
        return replies;
    }
}
