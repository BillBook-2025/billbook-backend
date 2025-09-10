package BillBook_2025_backend.backend.controller;

import BillBook_2025_backend.backend.dto.BookResponse;
import BillBook_2025_backend.backend.entity.Book;
import BillBook_2025_backend.backend.exception.UnauthorizedException;
import BillBook_2025_backend.backend.service.BookService;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import java.util.Map;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ml")
public class MLController {
    private final BookService bookService;
    private final RestTemplate restTemplate = new RestTemplate();
    // 추후 MLService 연동 가능
    // private final MLService mlService;

    public MLController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<BookResponse>> getRecommendedBooks(HttpSession session) {
        // 1. 로그인 여부 확인
        Object userIdObj = session.getAttribute("id");
        if (userIdObj == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        Long userId = (Long) userIdObj;

        // 2. DB에서 전체 책 조회
        List<BookResponse> allBooks = bookService.findAllBooks(userId);

        // 3. 임시 추천 로직: 최신 ID 기준으로 정렬 후 상위 2권 선택
        List<BookResponse> top2Books = allBooks.stream()
                .sorted(Comparator.comparing(BookResponse::getBookId).reversed()) // BookResponse 기준으로 수정
                .limit(2)
                .collect(Collectors.toList());

        // 4. 반환
        return ResponseEntity.ok(top2Books);
    }

    @PostMapping("/predict")
    public ResponseEntity<String> callFastAPI(@RequestBody Map<String, String> body) {
        String url = "http://localhost:8000/predict";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        Object result = response.getBody().get("result");
        return ResponseEntity.ok("ML 결과: " + (result != null ? result.toString() : "없음"));
    }
}
