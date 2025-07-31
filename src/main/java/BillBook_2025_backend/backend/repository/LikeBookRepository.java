package BillBook_2025_backend.backend.repository;

import BillBook_2025_backend.backend.entity.LikeBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeBookRepository extends JpaRepository<LikeBook, Long> {

    LikeBook save(LikeBook likeBook);
    Optional<LikeBook> findById(Long id);
    Optional<LikeBook> findByBookIdAndUserId(Long bookId, Long userId);

    List<LikeBook> findByBookId(Long bookId);

    @Query("select count(l.id) from LikeBook l where l.bookId = :bookId")
    Long countByBookId(@Param("bookId") Long bookId);
}
