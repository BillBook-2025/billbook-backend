package BillBook_2025_backend.backend.entity;

import jakarta.persistence.*;

@Entity
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Member follower;

    @ManyToOne
    private Member following;
}
