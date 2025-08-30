package BillBook_2025_backend.backend.controller;

import BillBook_2025_backend.backend.dto.*;
import BillBook_2025_backend.backend.entity.Book;
import BillBook_2025_backend.backend.exception.UnauthorizedException;
import BillBook_2025_backend.backend.service.ApiSearchingBook;
import BillBook_2025_backend.backend.service.BookService;
import BillBook_2025_backend.backend.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class BookController {
    private final BookService bookService;
    private final ApiSearchingBook apiSearchingBook;
    private final UserService userService;

    @Autowired
    public BookController(BookService bookService, ApiSearchingBook apiSearchingBook, UserService userService) {
        this.bookService = bookService;
        this.apiSearchingBook = apiSearchingBook;
        this.userService = userService;
    }

    @GetMapping("/api/books")
    public ResponseEntity<List<Book>> showAllBooks(HttpSession session){
        Long userId = (Long) session.getAttribute("id");
        if (userId == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        return ResponseEntity.ok(bookService.findAllBooks(userId));
    }


    @GetMapping("/api/books/{book_id}")
    public ResponseEntity<Book> showBook(@PathVariable Long book_id, HttpSession session){
        Long userId = (Long) session.getAttribute("id");
        return ResponseEntity.ok(bookService.getBookDetail(book_id, userId));
    }

    @PatchMapping("/api/books/{book_id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long book_id,@RequestBody Book book, HttpSession session
                                           ,@RequestPart(value = "deleteImages", required = false) List<String> deleteImages
                                           ,@RequestPart(value = "newImages", required = false) List<MultipartFile> files) throws IOException {
        Long userId = (Long) session.getAttribute("id");
        return ResponseEntity.ok(bookService.updateBookDetail(book, book_id, userId, deleteImages, files));
    }

    @GetMapping("/api/books/{book_id}/like")
    public ResponseEntity<LikeBookResponseDto> checkLike(@PathVariable Long book_id, HttpSession session){
        Long likeCount = bookService.checkLike(book_id);
        return ResponseEntity.ok(new LikeBookResponseDto(book_id, likeCount));
    }

    @PostMapping("/api/books/{book_id}/like")
    public ResponseEntity<LikeBookResponseDto> likePost(@PathVariable Long book_id, HttpSession session){
        Long userId = (Long) session.getAttribute("id");
        Long likeCount = bookService.like(book_id, userId);
        return ResponseEntity.ok(new LikeBookResponseDto(book_id, likeCount));
    }



    @ResponseBody
    @GetMapping("/api/books/register/openAPI")
    public List<BookItem> useBookOpenAPI(@RequestParam String keyword) {
        return apiSearchingBook.searchBook(keyword);
    }

    @PostMapping("/api/books/register/new")
    public ResponseEntity<String> register(@RequestBody BookPostRequestDto dto, HttpSession session, @RequestPart List<MultipartFile> files) throws IOException {
        Long userId = (Long) session.getAttribute("id");
        bookService.register(dto, userId, files);
        return ResponseEntity.ok("게시글이 성공적으로 등록되었습니다.");
    }

    @PostMapping("/api/books/{book_id}/borrow")
    public ResponseEntity<String> borrow(@PathVariable Long book_id, HttpSession session) {
        Long userId = (Long) session.getAttribute("id");
        bookService.borrow(book_id, userId);
        return ResponseEntity.ok("대출 신청이 완료되었습니다.");
    }

    @DeleteMapping("/api/books/{book_id}")
    public ResponseEntity<String> deletePost(@PathVariable Long book_id, HttpSession session) {
        Long userId = (Long) session.getAttribute("id");
        bookService.delete(book_id, userId);
        return ResponseEntity.ok("게시물이 삭제되었습니다.");
    }

    @PostMapping("/api/books/{book_id}/return")
    public ResponseEntity<String> returnBook(@PathVariable Long book_id, HttpSession session) {
        Long userId = (Long) session.getAttribute("id");
        bookService.returnBook(book_id, userId);
        return ResponseEntity.ok("반납처리가 완료되었습니다.");
    }

    @PostMapping("/api/books/{bookId}/upload-images")
    public ResponseEntity<PictureDtoList> uploadImages(@PathVariable Long book_id, HttpSession session, @RequestPart List<MultipartFile> files) throws IOException {
        Long userId = (Long) session.getAttribute("id");
        userService.checkBookSeller(userId, book_id);
        PictureDtoList pictureDtoList = bookService.uploadImages(book_id, userId, files);
        return ResponseEntity.ok(pictureDtoList);
    }

    @DeleteMapping("/api/books/{bookId}/upload-images")
    public ResponseEntity<String> deleteImages(@PathVariable Long book_id, HttpSession session, @RequestBody PictureDto request) {
        Long userId = (Long) session.getAttribute("id");
        userService.checkBookSeller(userId, book_id);
        bookService.deleteImages(request);
        return ResponseEntity.ok("ok");

    }


}
