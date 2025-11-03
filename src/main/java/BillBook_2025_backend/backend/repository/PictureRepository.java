package BillBook_2025_backend.backend.repository;

import BillBook_2025_backend.backend.entity.Book;
import BillBook_2025_backend.backend.entity.Board;
import BillBook_2025_backend.backend.entity.Picture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PictureRepository extends JpaRepository<Picture, Long> {
    Optional<Picture> findByFilename(String filename);

    Optional<Picture> findByBookAndUrl(Book book, String url);

    Optional<Picture> findByBoardAndUrl(Board board, String url);
}
