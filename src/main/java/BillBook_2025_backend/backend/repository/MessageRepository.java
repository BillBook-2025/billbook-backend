package BillBook_2025_backend.backend.repository;

import BillBook_2025_backend.backend.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
