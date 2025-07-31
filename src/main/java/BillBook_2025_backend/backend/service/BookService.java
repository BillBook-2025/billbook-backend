package BillBook_2025_backend.backend.service;

import BillBook_2025_backend.backend.dto.BookPostRequestDto;
import BillBook_2025_backend.backend.entity.Book;
import BillBook_2025_backend.backend.entity.BookStatus;
import BillBook_2025_backend.backend.entity.LikeBook;
import BillBook_2025_backend.backend.entity.User;
import BillBook_2025_backend.backend.exception.BookNotFoundException;
import BillBook_2025_backend.backend.exception.UnauthorizedException;
import BillBook_2025_backend.backend.repository.BookRepository;
import BillBook_2025_backend.backend.repository.LikeBookRepository;
import BillBook_2025_backend.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LikeBookRepository likeBookRepository;

    @Autowired
    public BookService(BookRepository bookRepository, UserRepository userRepository, LikeBookRepository likeBookRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.likeBookRepository = likeBookRepository;
    }

    public void register(BookPostRequestDto dto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UnauthorizedException("로그인한 사용자만 등록이 가능합니다."));

        Book book = new Book();
        book.setUserId(user.getId());
        book.setBookPoint(dto.getBookPoint());
        book.setBookPic(dto.getBookPic());
        book.setLocation(dto.getLocation());
        book.setStatus(BookStatus.PENDING);
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

    public List<Book> findAllBooks(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UnauthorizedException("로그인한 사용자만 이용이 가능합니다."));
        List<Book> bookList = bookRepository.findAll();
        return bookList;


    }

    public Book getBookDetail(Long bookId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UnauthorizedException("로그인한 사용자만 이용이 가능합니다."));
        if (bookRepository.findById(bookId).isEmpty()) {
            throw new BookNotFoundException("해당 책이 존재하지 않습니다.");
        } else {
            Book book = bookRepository.findById(bookId).get();
            return book;
        }
    }

    public Long like(Long bookId, Long userId) { //좋아요 누르기
        User user = userRepository.findById(userId).orElseThrow(() -> new UnauthorizedException("로그인한 사용자만 이용이 가능합니다."));
        if (bookRepository.findById(bookId).isEmpty()) {
            throw new BookNotFoundException("해당 책이 존재하지 않습니다.");
        } else {
            Optional<LikeBook> existing = likeBookRepository.findByBookIdAndId(bookId, userId);
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

    @Transactional
    public Book updateBookDetail(Book book, Long bookId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UnauthorizedException("로그인한 사용자만 수정이 가능합니다.")); //로그인x일때
        if (bookRepository.findById(bookId).isEmpty()) { //책 게시물이 존재하지 않을 경우
            throw new BookNotFoundException("해당 책이 존재하지 않습니다.");
        } else {
            if (!bookRepository.findById(bookId).get().getUserId().equals(user.getId())) { //판매자 아이디가 아닐 경우
                throw new AccessDeniedException("판매자만 수정할 수 있습니다");
            } else {
//                return bookRepository.update(bookId, book);
                Book findBook = bookRepository.findById(bookId).get();
                findBook.setBookPoint(book.getBookPoint());
                findBook.setBookPic(book.getBookPic());
                findBook.setLocation(book.getLocation());
                findBook.setContent(book.getContent());
                findBook.setTitle(book.getTitle());
                findBook.setAuthor(book.getAuthor());
                findBook.setPublisher(book.getPublisher());
                findBook.setIsbn(book.getIsbn());
                findBook.setCategory(book.getCategory());
                findBook.setDescription(book.getDescription());
                return findBook;
            }

        }
    }

    @Transactional
    public void borrow(Long bookId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UnauthorizedException("로그인한 사용자만 이용이 가능합니다."));
        if (bookRepository.findById(bookId).isEmpty()) {
            throw new BookNotFoundException("해당 책이 존재하지 않습니다.");
        } else {
            if (bookRepository.findById(bookId).get().getUserId().equals(user.getId())) {
                throw new AccessDeniedException("직접 올린 게시물은 대출할 수 없습니다.");
            } else {
                bookRepository.updateBorrowId(bookId, userId, BookStatus.BORROWING);
            }
        }
    }

    public void delete(Long bookId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UnauthorizedException("로그인한 사용자만 이용이 가능합니다."));
        if (bookRepository.findById(bookId).isEmpty()) {
            throw new BookNotFoundException("해당 책이 존재하지 않습니다.");
        } else {
            if (!bookRepository.findById(bookId).get().getUserId().equals(user.getId())) {
                throw new AccessDeniedException("판매자만 글을 삭제할 수 있습니다");
            } else {
                bookRepository.delete(bookRepository.findById(bookId).get());
            }
        }
    }

    @Transactional
    public void returnBook(Long bookId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UnauthorizedException("로그인한 사용자만 이용이 가능합니다."));
        if (bookRepository.findById(bookId).isEmpty()) {
            throw new BookNotFoundException("해당 책이 존재하지 않습니다.");
        } else {
            if (!bookRepository.findById(bookId).get().getUserId().equals(user.getId())) {
                throw new AccessDeniedException("판매자만 반납완료를 처리할 수 있습니다");
            } else {
                bookRepository.updateStatus(bookId, BookStatus.RETURNED); //반납처리 완료
            }
        }
    }
}
