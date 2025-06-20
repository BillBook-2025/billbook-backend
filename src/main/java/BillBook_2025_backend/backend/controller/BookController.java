package BillBook_2025_backend.backend.controller;

import BillBook_2025_backend.backend.dto.BookItem;
import BillBook_2025_backend.backend.dto.BookPostRequestDto;
import BillBook_2025_backend.backend.entity.Book;
import BillBook_2025_backend.backend.entity.LikeBookResponseDto;
import BillBook_2025_backend.backend.exception.UnauthorizedException;
import BillBook_2025_backend.backend.service.ApiSearchingBook;
import BillBook_2025_backend.backend.service.BookService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class BookController {
    private final BookService bookService;
    private final ApiSearchingBook apiSearchingBook;

    @Autowired
    public BookController(BookService bookService, ApiSearchingBook apiSearchingBook) {
        this.bookService = bookService;
        this.apiSearchingBook = apiSearchingBook;
    }

    @GetMapping("/api/books")
    public ResponseEntity<List<Book>> showAllBooks(HttpSession session){
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        String userId = userIdObj.toString();
        return ResponseEntity.ok(bookService.findAllBooks(userId));
    }


    @GetMapping("/api/books/{book_id}")
    public ResponseEntity<Book> showBook(@PathVariable Long book_id, HttpSession session){
        String userId = session.getAttribute("userId").toString();
        return ResponseEntity.ok(bookService.getBookDetail(book_id, userId));

    }

    @PatchMapping("/api/books/{book_id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long book_id,@RequestBody Book book, HttpSession session){
        String userId = session.getAttribute("userId").toString();
        return ResponseEntity.ok(bookService.updateBookDetail(book, book_id, userId));
    }

    @GetMapping("/api/books/{book_id}/like")
    public ResponseEntity<LikeBookResponseDto> checkLike(@PathVariable Long book_id, HttpSession session){
        Long likeCount = bookService.checkLike(book_id);
        return ResponseEntity.ok(new LikeBookResponseDto(book_id, likeCount));
    }

    @PostMapping("/api/books/{book_id}/like")
    public ResponseEntity<LikeBookResponseDto> likePost(@PathVariable Long book_id, HttpSession session){
        String userId = session.getAttribute("userId").toString();
        Long likeCount = bookService.like(book_id, userId);
        return ResponseEntity.ok(new LikeBookResponseDto(book_id, likeCount));
    }



    @ResponseBody
    @GetMapping("/api/books/register/openAPI")
    public List<BookItem> useBookOpenAPI(@RequestParam String keyword) {
        return apiSearchingBook.searchBook(keyword);
    }

    @PostMapping("/api/books/register/new")
    public ResponseEntity<String> register(@RequestBody BookPostRequestDto dto, HttpSession session) {
        String userId = session.getAttribute("userId").toString();
        bookService.register(dto, userId);
        return ResponseEntity.ok("게시글이 성공적으로 등록되었습니다.");
    }

    @PostMapping("/api/books/{book_id}/borrow")
    public ResponseEntity<String> borrow(@PathVariable Long book_id, HttpSession session) {
        String userId = session.getAttribute("userId").toString();
        bookService.borrow(book_id, userId);
        return ResponseEntity.ok("대출 신청이 완료되었습니다.");
    }

    @DeleteMapping("/api/books/{book_id}")
    public ResponseEntity<String> deletePost(@PathVariable Long book_id, HttpSession session) {
        String userId = session.getAttribute("userId").toString();
        bookService.delete(book_id, userId);
        return ResponseEntity.ok("게시물이 삭제되었습니다.");
    }

    @PostMapping("/api/books/{book_id}/return")
    public ResponseEntity<String> returnBook(@PathVariable Long book_id, HttpSession session) {
        String userId = session.getAttribute("userId").toString();
        bookService.returnBook(book_id, userId);
        return ResponseEntity.ok("반납처리가 완료되었습니다.");
    }
}
