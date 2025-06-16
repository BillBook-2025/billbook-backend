package BillBook_2025_backend.backend.repository;

import BillBook_2025_backend.backend.entity.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {

    Book save(Book book);
    Optional<Book> findById(Long id);
    List<Book> findByUserId(String userId); //빌린 사람 아이디로 게시물 찾기
    List<Book> findByBorrowId(String borrowId);  //빌려준 사람 아이디로 게시물 찾기
    List<Book> findByTitle(String title);
    List<Book> findByAuthor(String author);
    List<Book> findAll();
    Book update(Long id);
    Book update(Long id, String borrowId);
    Book update(Long id, Book book);
    void delete(Long id);

}
