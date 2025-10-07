package BillBook_2025_backend.backend.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class IamportClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${imp.key}")
    private String apiKey;

    @Value("${imp.secretKey}")
    private String apiSecret;

    private String getAccessToken() {
        String url = "https://api.iamport.kr/users/getToken";

        Map<String, String> body = Map.of(
                "imp_key", apiKey,
                "imp_secret", apiSecret
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(url, body, Map.class);
        return (String) ((Map) response.getBody().get("response")).get("access_token");
    }

    public PaymentInfo getPaymentInfo(String impUid) {
        String token = getAccessToken();
        String url = "https://api.iamport.kr/payments/" + impUid;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        Map<String, Object> res = (Map<String, Object>) response.getBody().get("response");

        return new PaymentInfo(
                (String) res.get("imp_uid"),
                ((Number) res.get("amount")).intValue(),
                (String) res.get("status"),
                (String) res.get("merchant_uid")
        );
    }
}
