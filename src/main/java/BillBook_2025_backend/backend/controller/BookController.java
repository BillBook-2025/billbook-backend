package BillBook_2025_backend.backend.controller;

import BillBook_2025_backend.backend.dto.BookItem;
import BillBook_2025_backend.backend.dto.BookPostRequestDto;
import BillBook_2025_backend.backend.service.ApiSearchingBook;
import BillBook_2025_backend.backend.service.BookService;
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
    @ResponseBody
    @GetMapping("/api/books/register/openAPI")
    public List<BookItem> useBookOpenAPI(@RequestParam String keyword) {
        return apiSearchingBook.searchBook(keyword);
    }

    @PostMapping("/api/books/register/new")
    public ResponseEntity<String> register(@RequestBody BookPostRequestDto dto) {
        bookService.register(dto);
        return ResponseEntity.ok("게시글이 성공적으로 등록되었습니다.");
    }
}
