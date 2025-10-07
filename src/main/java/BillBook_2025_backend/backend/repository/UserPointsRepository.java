package BillBook_2025_backend.backend.repository;

import BillBook_2025_backend.backend.entity.UserPoints;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPointsRepository extends JpaRepository<UserPoints, Long> {
}
