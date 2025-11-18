package BillBook_2025_backend.backend.controller;

import BillBook_2025_backend.backend.dto.*;
import BillBook_2025_backend.backend.entity.Book;
import BillBook_2025_backend.backend.exception.UnauthorizedException;
import BillBook_2025_backend.backend.service.ApiSearchingBook;
import BillBook_2025_backend.backend.service.BookService;
import BillBook_2025_backend.backend.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<List<BookResponse>> showAllBooks(HttpSession session){
        Long userId = (Long) session.getAttribute("id");
        if (userId == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        return ResponseEntity.ok(bookService.findAllBooks(userId));
    }


    @GetMapping("/api/books/{bookId}")
    public ResponseEntity<BookResponse> showBook(@PathVariable Long bookId, HttpSession session){
        Long userId = (Long) session.getAttribute("id");
        return ResponseEntity.ok(bookService.getBookDetail(bookId, userId));
    }

    @PatchMapping(
            value = "/api/books/{bookId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Map<String, String>> updateBook(@PathVariable Long bookId, @RequestPart("book") BookPostRequestDto book, HttpSession session
                                           , @RequestPart(value = "deleteImages", required = false) List<String> deleteImages
                                           , @RequestPart(value = "newImages", required = false) List<MultipartFile> files) throws IOException {
        Long userId = (Long) session.getAttribute("id");
        bookService.updateBookDetail(book, bookId, userId, deleteImages, files);
        return ResponseEntity.ok(Map.of("message", "거래글이 수정되었습니다."));
    }

    @GetMapping("/api/books/{bookId}/like")
    public ResponseEntity<LikeBookResponseDto> checkLike(@PathVariable Long bookId, HttpSession session){
        Long likeCount = bookService.checkLike(bookId);
        return ResponseEntity.ok(new LikeBookResponseDto(bookId, likeCount));
    }

    @PostMapping("/api/books/{bookId}/like")
    public ResponseEntity<LikeBookResponseDto> likePost(@PathVariable Long bookId, HttpSession session){
        Long userId = (Long) session.getAttribute("id");
        Long likeCount = bookService.like(bookId, userId);
        return ResponseEntity.ok(new LikeBookResponseDto(bookId, likeCount));
    }



    @ResponseBody
    @GetMapping("/api/books/register/new/info")
    public List<BookItem> useBookOpenAPI(@RequestParam String keyword) {
        return apiSearchingBook.searchBook(keyword);
    }

    @PostMapping("/api/books/register/new")
    public ResponseEntity<String> register(@RequestPart("book") BookPostRequestDto dto, HttpSession session, @RequestPart("images") List<MultipartFile> files) throws IOException {
        Long userId = (Long) session.getAttribute("id");
        bookService.register(dto, userId, files);
        return ResponseEntity.ok("게시글이 성공적으로 등록되었습니다.");
    }

    @PostMapping("/api/books/{bookId}/borrow")
    public ResponseEntity<String> borrow(@PathVariable Long bookId, HttpSession session) {
        Long userId = (Long) session.getAttribute("id");
        bookService.borrow(bookId, userId);
        return ResponseEntity.ok("대출 신청이 완료되었습니다.");
    }

    @DeleteMapping("/api/books/{bookId}")
    public ResponseEntity<String> deletePost(@PathVariable Long bookId, HttpSession session) {
        Long userId = (Long) session.getAttribute("id");
        bookService.delete(bookId, userId);
        return ResponseEntity.ok("게시물이 삭제되었습니다.");
    }

    @PostMapping("/api/books/{bookId}/existing")
    public ResponseEntity<BookResponse> returnBook(@PathVariable Long bookId, HttpSession session) {
        Long userId = (Long) session.getAttribute("id");
        BookResponse bookResponse = bookService.returnBook(bookId, userId);
        return ResponseEntity.ok(bookResponse);
    }

    @PostMapping("/api/books/{bookId}/upload-images")
    public ResponseEntity<PictureDtoList> uploadImages(@PathVariable Long bookId, HttpSession session, @RequestPart List<MultipartFile> files) throws IOException {
        Long userId = (Long) session.getAttribute("id");
        userService.checkBookSeller(userId, bookId);
        PictureDtoList pictureDtoList = bookService.uploadImages(bookId, userId, files);
        return ResponseEntity.ok(pictureDtoList);
    }

    @DeleteMapping("/api/books/{bookId}/upload-images")
    public ResponseEntity<String> deleteImages(@PathVariable Long bookId, HttpSession session, @RequestBody PictureDto request) {
        Long userId = (Long) session.getAttribute("id");
        userService.checkBookSeller(userId, bookId);
        bookService.deleteImages(request);
        return ResponseEntity.ok("ok");

    }

    @PostMapping("/api/books/search")
    public ResponseEntity<List<BookResponse>> searchBooks(@RequestParam String keyword, HttpSession session){
        Long userId = (Long) session.getAttribute("id");
        List<BookResponse> bookListResponse = bookService.searchBooks(keyword, userId);
        return ResponseEntity.ok(bookListResponse);
    }


}
