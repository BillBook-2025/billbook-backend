package BillBook_2025_backend.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.catalina.User;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "user_points")
public class UserPoints {

    @Id
    private Long memberId;

    private Integer balance = 0;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    @OneToOne
    @MapsId   // userId가 Member의 PK를 그대로 사용
    @JoinColumn(name = "member_id")
    private Member member;

    public UserPoints() {}

    public UserPoints(Long userId, Integer balance) {
        this.memberId = userId;
        this.balance = balance;
    }
    public void charge(int amount) {
        this.balance += amount;
    }

    public void use(int amount) {
        if (this.balance < amount) {
            throw new IllegalStateException("포인트 부족");
        }
        this.balance -= amount;
    }

    public Integer getBalance() {
        return this.balance;
    }
}
