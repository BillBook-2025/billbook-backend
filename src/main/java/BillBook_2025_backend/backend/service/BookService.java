package BillBook_2025_backend.backend.service;

import BillBook_2025_backend.backend.dto.BookPostRequestDto;
import BillBook_2025_backend.backend.entity.Book;
import BillBook_2025_backend.backend.entity.LikeBook;
import BillBook_2025_backend.backend.entity.User;
import BillBook_2025_backend.backend.exception.BookNotFoundException;
import BillBook_2025_backend.backend.exception.UnauthorizedException;
import BillBook_2025_backend.backend.repository.BookRepository;
import BillBook_2025_backend.backend.repository.LikeBookRepository;
import BillBook_2025_backend.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LikeBookRepository likeBookRepository;

    public BookService(BookRepository bookRepository, UserRepository userRepository, LikeBookRepository likeBookRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.likeBookRepository = likeBookRepository;
    }

    public void register(BookPostRequestDto dto, String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new UnauthorizedException("로그인한 사용자만 등록이 가능합니다."));

        Book book = new Book();
        book.setUserId(user.getUserId());
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

    public List<Book> findAllBooks(String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new UnauthorizedException("로그인한 사용자만 이용이 가능합니다."));
        List<Book> bookList = bookRepository.findAll();
        return bookList;


    }

    public Book getBookDetail(Long bookId, String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new UnauthorizedException("로그인한 사용자만 이용이 가능합니다."));
        if (bookRepository.findById(bookId).isEmpty()) {
            throw new BookNotFoundException("해당 책이 존재하지 않습니다.");
        } else {
            Book book = bookRepository.findById(bookId).get();
            return book;
        }
    }

    public Long like(Long bookId, String userId) { //좋아요 누르기
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new UnauthorizedException("로그인한 사용자만 이용이 가능합니다."));
        if (bookRepository.findById(bookId).isEmpty()) {
            throw new BookNotFoundException("해당 책이 존재하지 않습니다.");
        } else {
            Optional<LikeBook> existing = likeBookRepository.findByBookIdAndUserId(bookId, userId);
            if (existing.isPresent()) { //좋아요 취소
                likeBookRepository.delete(existing.get());
            } else { //좋아요
                LikeBook likeBook = new LikeBook(bookId, userId);
                likeBookRepository.save(likeBook);
            }
            return likeBookRepository.countByBookId(bookId);
        }
    }

    public Long checkLike(Long bookId) {
        if (bookRepository.findById(bookId).isEmpty()) {
            throw new BookNotFoundException("해당 책이 존재하지 않습니다.");
        } else {
            return likeBookRepository.countByBookId(bookId);
        }
    }

    public Book updateBookDetail(Book book, Long bookId, String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new UnauthorizedException("로그인한 사용자만 수정이 가능합니다.")); //로그인x일때
        if (bookRepository.findById(bookId).isEmpty()) { //책 게시물이 존재하지 않을 경우
            throw new BookNotFoundException("해당 책이 존재하지 않습니다.");
        } else {
            if (!bookRepository.findById(bookId).get().getUserId().equals(user.getUserId())) { //판매자 아이디가 아닐 경우
                throw new AccessDeniedException("판매자만 수정할 수 있습니다");
            } else {
                return bookRepository.update(bookId, book);
            }

        }
    }

    public void borrow(Long bookId, String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new UnauthorizedException("로그인한 사용자만 이용이 가능합니다."));
        if (bookRepository.findById(bookId).isEmpty()) {
            throw new BookNotFoundException("해당 책이 존재하지 않습니다.");
        } else {
            if (bookRepository.findById(bookId).get().getUserId().equals(user.getUserId())) {
                throw new AccessDeniedException("직접 올린 게시물은 대출할 수 없습니다.");
            } else {
                bookRepository.update(bookId, userId);
            }
        }
    }

    public void delete(Long bookId, String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new UnauthorizedException("로그인한 사용자만 이용이 가능합니다."));
        if (bookRepository.findById(bookId).isEmpty()) {
            throw new BookNotFoundException("해당 책이 존재하지 않습니다.");
        } else {
            if (!bookRepository.findById(bookId).get().getUserId().equals(user.getUserId())) {
                throw new AccessDeniedException("판매자만 글을 삭제할 수 있습니다");
            } else {
                bookRepository.delete(bookId);
            }
        }
    }

    public void returnBook(Long bookId, String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new UnauthorizedException("로그인한 사용자만 이용이 가능합니다."));
        if (bookRepository.findById(bookId).isEmpty()) {
            throw new BookNotFoundException("해당 책이 존재하지 않습니다.");
        } else {
            if (!bookRepository.findById(bookId).get().getUserId().equals(user.getUserId())) {
                throw new AccessDeniedException("판매자만 반납완료를 처리할 수 있습니다");
            } else {
                bookRepository.update(bookId); //반납처리 완료
            }
        }
    }
}
