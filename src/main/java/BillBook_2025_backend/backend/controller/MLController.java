// import org.springframework.http.*;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.client.RestTemplate;
// import java.util.*;

// // @RestController
// // @RequestMapping("/api/books/recommendations")
// // public class MLController {

// //     private final RestTemplate restTemplate = new RestTemplate();

// //     @PostMapping("/predict")
// //     public ResponseEntity<String> callFastAPI(@RequestBody Map<String, String> body) {
// //         String url = "http://localhost:8000/predict";

// //         HttpHeaders headers = new HttpHeaders();
// //         headers.setContentType(MediaType.APPLICATION_JSON);

// //         HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
// //         ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

// //         return ResponseEntity.ok("ML 결과: " + response.getBody().get("result"));
// //     }
// // }


// src/main/java/BillBook_2025_backend/backend/controller/MLController.java

package BillBook_2025_backend.backend.controller;

import BillBook_2025_backend.backend.entity.Book;
import BillBook_2025_backend.backend.exception.UnauthorizedException;
import BillBook_2025_backend.backend.service.BookService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ml")
public class MLController {

    private final BookService bookService;

    // You can also inject MLService here for future use
    // private final MLService mlService;

    public MLController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<Book>> getRecommendedBooks(HttpSession session) {
        // 1. Check if the user is logged in
        Object userIdObj = session.getAttribute("id");
        if (userIdObj == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        Long userId = (Long) userIdObj;

        // 2. Get all books from the database
        List<Book> allBooks = bookService.findAllBooks(userId);

        // 3. (Temporary) Sort the books and get the top 2
        // Replace this with your actual recommendation logic later
        List<Book> top2Books = allBooks.stream()
                .sorted(Comparator.comparing(Book::getId).reversed())
                .limit(2)
                .collect(Collectors.toList());

        // 4. Return the top 2 books
        return ResponseEntity.ok(top2Books);
    }
}