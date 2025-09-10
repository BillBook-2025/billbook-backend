// 걍 controller로도 fastapi 쪽으로 요청 보낼 수 있긴한데.. 요청 전에 전처리를 한다거나 할까바
package BillBook_2025_backend.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Service
public class MLService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String predict(Map<String, String> input) {
        String url = "http://localhost:8000/predict";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(input, headers);
        
        // 여기서 resttemplate로 post 보내고 응답까지 받아오는거네
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        return response.getBody().get("result").toString(); // 응답 결과 다시 controller로
    }
}
