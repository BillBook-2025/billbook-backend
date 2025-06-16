package BillBook_2025_backend.backend.repository;

import BillBook_2025_backend.backend.entity.LikeBook;

import java.util.List;
import java.util.Optional;

public interface LikeBookRepository {

    LikeBook save(LikeBook likeBook);
    Optional<LikeBook> findById(Long id);
    Optional<LikeBook> findByBookIdAndUserId(Long bookId, String userId);
    //List<LikePost> findAll();
    List<LikeBook> findByBookId(Long bookId);
    Long countByBookId(Long bookId);
    void delete(LikeBook likeBook);
}
