package BillBook_2025_backend.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.catalina.User;

@Entity
@Getter
@Setter
public class LikeBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Board board;

    @ManyToOne
    private Member member;

    protected LikeBoard() {}

    public LikeBoard(Board board, Member member) {
        this.board = board;
        this.member = member;
    }

}
