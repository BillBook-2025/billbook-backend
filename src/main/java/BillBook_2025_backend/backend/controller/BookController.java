package BillBook_2025_backend.backend.controller;

import BillBook_2025_backend.backend.dto.BookItem;
import BillBook_2025_backend.backend.dto.BookPostRequestDto;
import BillBook_2025_backend.backend.entity.Book;
import BillBook_2025_backend.backend.entity.LikeBookResponseDto;
import BillBook_2025_backend.backend.service.ApiSearchingBook;
import BillBook_2025_backend.backend.service.BookService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Book> showAllBooks(HttpSession session){
        String userId = session.getAttribute("userId").toString();
        return bookService.findAllBooks(userId);
    }

    @ResponseBody
    @GetMapping("/api/books/{book_id}")
    public Book showBook(@PathVariable Long book_id, HttpSession session){
        String userId = session.getAttribute("userId").toString();
        return bookService.getBookDetail(book_id, userId);

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


}
