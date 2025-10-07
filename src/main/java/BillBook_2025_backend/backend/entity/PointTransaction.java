package BillBook_2025_backend.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private Integer amount;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private String impUid;

    private LocalDateTime createdAt = LocalDateTime.now();

    public void success() {
        this.status = TransactionStatus.SUCCESS;
    }

    public void fail() {
        this.status = TransactionStatus.FAIL;
    }

    public void setStatus(TransactionStatus transactionStatus) {
        this.status = transactionStatus;
    }
}
