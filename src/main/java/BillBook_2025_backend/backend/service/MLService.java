// 걍 controller로도 fastapi 쪽으로 요청 보낼 수 있긴한데.. 요청 전에 전처리를 한다거나 할까바
package BillBook_2025_backend.backend.service;

import BillBook_2025_backend.backend.dto.*;
import BillBook_2025_backend.backend.entity.Book;
import BillBook_2025_backend.backend.service.UserService;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Comparator;

@Service
public class MLService {
    private final UserService userService;  // 필드 선언

    @Autowired
    public MLService(UserService userService) {
        this.userService = userService;
    }

    public String getBookQuery(Long userId) {
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
}
