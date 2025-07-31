package BillBook_2025_backend.backend.repository;

import BillBook_2025_backend.backend.entity.LikeBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeBookRepository extends JpaRepository<LikeBook, Long> {

    LikeBook save(LikeBook likeBook);
    Optional<LikeBook> findById(Long id);
    Optional<LikeBook> findByBookIdAndId(Long bookId, Long userId);
    //List<LikePost> findAll();
    List<LikeBook> findByBookId(Long bookId);
    Long countByBookId(Long bookId);
    void delete(LikeBook likeBook);
}
