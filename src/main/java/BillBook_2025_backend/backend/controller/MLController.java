package BillBook_2025_backend.backend.controller;

import BillBook_2025_backend.backend.dto.*;
import BillBook_2025_backend.backend.exception.UnauthorizedException;
import BillBook_2025_backend.backend.service.BookService;
import BillBook_2025_backend.backend.service.UserService;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/ml")
public class MLController {
    private final BookService bookService;
    private final UserService userService; // buyList 가져오기용
    private final RestTemplate restTemplate = new RestTemplate();

    public MLController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }

    // -------------------------------
    // 1️⃣ 최근 구매 아이템 가져오기
    // -------------------------------
    private String getLatestBuyQuery(Long userId) {
        DataResponse buyListResponse = userService.getBuyList(userId);
        BookListResponse bookList = buyListResponse.getData();

        List<BookResponse> books = bookList.getBooks(); // 리스트 꺼내기
        if (books == null || books.isEmpty()) {
            return "";
            // return "Title: 샘플책 Category: 판타지 Description: 테스트용 더미데이터";
        }

        BookResponse latestBook = books.get(0); // 최신순이라고 가정

        return String.format(
            "Title: %s Category: %s Description: %s",
            latestBook.getTitle(),
            String.join(", ", latestBook.getCategory()), // category가 리스트면
            latestBook.getDescription()
        );
    }

    // -------------------------------
    // 2️⃣ FastAPI 호출
    // -------------------------------
    @GetMapping("/recommendations")
    public ResponseEntity<Map<String, Object>> searchML(HttpSession session) {
        Object userIdObj = session.getAttribute("id");
        if (userIdObj == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        Long userId = (Long) userIdObj;

        String userQuery = getLatestBuyQuery(userId);

        String url = "http://3.34.1.69:8000/ml/search?query=" + UriUtils.encode(userQuery, StandardCharsets.UTF_8);

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        Map<String, Object> result = response.getBody();

        return ResponseEntity.ok(result); // FastAPI JSON 그대로 반환
    }
}