package BillBook_2025_backend.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PaymentInfo {
    private String impUid;
    private Integer amount;
    private String status;
    private String merchantUid;
}

