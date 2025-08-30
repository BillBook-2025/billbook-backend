package BillBook_2025_backend.backend.repository;

import BillBook_2025_backend.backend.entity.Book;
import BillBook_2025_backend.backend.entity.LikeBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeBookRepository extends JpaRepository<LikeBook, Long> {

    List<LikeBook> findByMemberId(Long userId);
    Optional<LikeBook> findByBookIdAndMemberId(Long bookId, Long memberId);

    List<LikeBook> findByBookId(Long bookId);

    @Query("select count(l.id) from LikeBook l where l.book = :book")
    Long countByBookId(@Param("book") Book book);
}
