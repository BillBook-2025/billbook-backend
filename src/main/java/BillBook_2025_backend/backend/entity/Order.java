package BillBook_2025_backend.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private ItemPoint itemName;
    private Integer amount;
    private String payMethod = "card";

    @Column(name = "paid_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime paidAt;

    private String email;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private String buyerName;
    private String buyerEmail;
    private String buyerPhone;
    private String buyerAddress;
    private String buyerPostCode;


    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String merchantUid; //결제 전 임시 id

    private String impUid = null; //결제 번호

    public Order() {}

    public Order(Member member) {
        this.member = member;
        this.buyerAddress = member.getAddress();
        this.buyerEmail = member.getEmail();
        this.buyerPhone = member.getPhone();
        this.buyerName = member.getRealName();
        this.buyerPostCode = member.getPost_code();
    }

    public void updateBySuccess(String impUid){
        this.status = PaymentStatus.DONE;
        this.impUid = impUid;
    }

}
