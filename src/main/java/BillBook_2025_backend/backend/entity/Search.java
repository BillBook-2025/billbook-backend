package BillBook_2025_backend.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Search {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String keyword;
    private LocalDateTime searchTime;

    @ManyToOne
    private User user;

}
