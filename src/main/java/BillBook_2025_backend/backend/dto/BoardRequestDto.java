package BillBook_2025_backend.backend.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class BoardRequestDto {
    private String title;
    private String category; // "리뷰" | "나눔" | "기타"
    private String isbn; // 책 정보
    // private String boardPic;  //자료형 나중에 체크
    private List<PictureDto> bookPic = new ArrayList<>();;  //자료형 나중에 체크
    private String content;
}