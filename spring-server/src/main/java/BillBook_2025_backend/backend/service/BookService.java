package BillBook_2025_backend.backend.service;

import BillBook_2025_backend.backend.dto.BookPostRequestDto;
import BillBook_2025_backend.backend.entity.Book;
import BillBook_2025_backend.backend.repository.BookRepository;
import org.springframework.stereotype.Service;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void register(BookPostRequestDto dto) {

        Book book = new Book();
        book.setUserId(dto.getUserId());
        book.setBookPoint(dto.getBookPoint());
        book.setBookPic(dto.getBookPic());
        book.setLocation(dto.getLocation());
        book.setContent(dto.getContent());
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setPublisher(dto.getPublisher());
        book.setIsbn(dto.getIsbn());
        //book.setCategory(dto.getCategory());
        book.setDescription(dto.getDescription());
       // book.setTotal(dto.getTotal());

        bookRepository.save(book);
    }
}
