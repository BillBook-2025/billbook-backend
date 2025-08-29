package BillBook_2025_backend.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DealHistory {
    private Long dealNum;

    public DealHistory(Long dealNum) {
        this.dealNum = dealNum;
    }
}
