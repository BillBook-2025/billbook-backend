package BillBook_2025_backend.backend.service;

import BillBook_2025_backend.backend.dto.BookApiResponse;
import BillBook_2025_backend.backend.dto.BookItem;
import BillBook_2025_backend.backend.exception.BookNotFoundException;
import BillBook_2025_backend.backend.exception.BookSearchException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApiSearchingBook {

    public List<BookItem> searchBook(String keyword) {
        final String clientId = "IRZib36SGbfmLNNf_Myt"; //애플리케이션 클라이언트 아이디
        final String clientSecret = "ivgaKLIJCm"; //애플리케이션 클라이언트 시크릿
        ObjectMapper objectMapper = new ObjectMapper(); // json 문자열을 객체로 변환하기 위한 변수
        String encodedKeyword = "";
        URL url;
        try {
            encodedKeyword = URLEncoder.encode(keyword, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패",e);
        }
        String apiURL = "https://openapi.naver.com/v1/search/book.json?query=" + encodedKeyword;
        try {
            url = new URL(apiURL);
        } catch (MalformedURLException e) {
            throw new BookSearchException("URL 형식이 잘못되었습니다.", e);
        }

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        String responseBody = get(apiURL,requestHeaders);

        try {
            BookApiResponse response = objectMapper.readValue(responseBody, BookApiResponse.class);
            if (response.getItems() == null || response.getItems().isEmpty()) {
                throw new BookNotFoundException("해당 제목의 책이 없습니다.");
            }
            return response.getItems();
        } catch (IOException e) {
            throw new BookSearchException("도서 정보를 불러오는 데 실패했습니다.", e);
        }

    }

    private String get(String apiUrl, Map<String, String> requestHeaders){
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }


            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 오류 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);


        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();


            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }


            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는 데 실패했습니다.", e);
        }
    }
}
