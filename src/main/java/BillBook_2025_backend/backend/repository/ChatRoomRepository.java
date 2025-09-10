package BillBook_2025_backend.backend.repository;

import BillBook_2025_backend.backend.entity.Book;
import BillBook_2025_backend.backend.entity.ChatRoom;
import BillBook_2025_backend.backend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("select c from ChatRoom c where c.book = :book and c.buyer = :buyer")
    Optional<ChatRoom> findByBookAndBuyer(Book book, Member buyer);

    @Query("select c from ChatRoom c where c.seller = :member or c.buyer = :member")
    List<ChatRoom> findByMember(Member member);
}
