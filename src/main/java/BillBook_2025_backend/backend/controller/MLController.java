package BillBook_2025_backend.backend.controller;

import BillBook_2025_backend.backend.dto.*;
import BillBook_2025_backend.backend.exception.UnauthorizedException;
import BillBook_2025_backend.backend.service.MLService;
import BillBook_2025_backend.backend.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/ml")
public class MLController {
    private final MLService mlService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public MLController(MLService mlService) {
        this.mlService = mlService;
    }

    @GetMapping("/recommendations")
    public ResponseEntity<Map<String, Object>> searchML(HttpSession session) {
        Object userIdObj = session.getAttribute("id");
        if (userIdObj == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        
        Long userId = (Long) userIdObj;
        String userQuery = mlService.getBookQuery(userId);

        String url = "http://3.34.1.69:8000/ml/search?query=" + UriUtils.encode(userQuery, StandardCharsets.UTF_8);
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        Map<String, Object> result = response.getBody();

        return ResponseEntity.ok(result); // FastAPI JSON 그대로 반환
    }
}