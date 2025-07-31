package BillBook_2025_backend.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BookStatus {
    PENDING(0,"거래가 아직 이루어지지 않은 상태"),
    BORROWING(1, "대여중"),
    RETURNED(2, "반납 완료");


    private int id;
    private String status;
}
