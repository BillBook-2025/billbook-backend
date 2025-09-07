package BillBook_2025_backend.backend.repository;

import BillBook_2025_backend.backend.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatRoomId(Long chatRoomId);

    @Query("select m from Message m where m.chatRoom.id = :chatRoomId order by m.id desc")
    Page<Message> findPageByChatRoomId(Long chatRoomId, Pageable pageable);
}
