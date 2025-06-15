import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@RestController
@RequestMapping("/api/books/recommendations")
public class MLController {

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/predict")
    public ResponseEntity<String> callFastAPI(@RequestBody Map<String, String> body) {
        String url = "http://localhost:8000/predict";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        return ResponseEntity.ok("ML 결과: " + response.getBody().get("result"));
    }
}