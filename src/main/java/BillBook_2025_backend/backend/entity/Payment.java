package BillBook_2025_backend.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Entity
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long price;

    @Column(name = "paid_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime paidAt;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Member seller;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private Member buyer;

    @OneToOne
    private Book book;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String merchantUid; //결제 전 임시 id

    private String impUid = null; //결제 번호

    public Payment() {}

    public Payment(Member seller, Member buyer, Book book) {
        this.seller = seller;
        this.buyer = buyer;
        this.book = book;
        this.price = book.getBookPoint();
    }

    public void updateBySuccess(String impUid){
        this.status = PaymentStatus.DONE;
        this.impUid = impUid;
    }

}
