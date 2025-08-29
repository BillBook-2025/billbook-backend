package BillBook_2025_backend.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Member follower;

    @ManyToOne
    private Member following;

    public Follow() {}
    public Follow(Member follower, Member following) {
        this.follower = follower;
        this.following = following;
    }
}
