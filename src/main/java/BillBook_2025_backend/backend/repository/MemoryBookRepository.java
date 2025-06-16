package BillBook_2025_backend.backend.repository;

import BillBook_2025_backend.backend.entity.Book;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MemoryBookRepository implements BookRepository {
    private final Map<Long, Book> store = new HashMap<>();
    private Long nextId = 1L;

    public Book save(Book book){
        book.setId(nextId++);
        store.put(book.getId(), book);
        return book;
    }


    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Book> findByUserId(String userId) {
        return store.values().stream()
                .filter(book -> book.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Book> findByBorrowId(String borrowId){
        List<Book> books = store.values().stream()
                .filter(book-> book.getUserId().equals(borrowId))
                .collect(Collectors.toList());
        return books;
    }

    public List<Book> findByTitle(String title) {
        List<Book> books = store.values().stream()
                .filter(book-> book.getTitle().equals(title))
                .collect(Collectors.toList());
        return books;
    }

    public List<Book> findByAuthor(String author) {
        List<Book> books = store.values().stream()
                .filter(book-> book.getAuthor().equals(author))
                .collect(Collectors.toList());
        return books;
    }

    public List<Book> findAll() {
        List<Book> books = store.values().stream().toList();
        return books;
    }

    public Book update(Long id) {
        Book returnedBook = store.get(id);
        returnedBook.setStatus("반납");
        store.put(id, returnedBook);
        return returnedBook;
    }

    public Book update(Long id, String borrowId) { //대출한 사람 아이디 삽입
        Book bookToUpdate = store.get(id);
        bookToUpdate.setBorrowId(borrowId);
        store.put(id, bookToUpdate);
        return bookToUpdate;
    }



    public Book update(Long id, Book book) {
        Book bookToUpdate = store.get(id);
        bookToUpdate.setBookPoint(book.getBookPoint());
        bookToUpdate.setBookPic(book.getBookPic());
        bookToUpdate.setLocation(book.getLocation());
        bookToUpdate.setContent(book.getContent());

        store.put(id, bookToUpdate);
        return bookToUpdate;
    }

    public void delete(Long id) {
        store.remove(id);
    }
}
