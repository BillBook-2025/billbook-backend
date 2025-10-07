package BillBook_2025_backend.backend.repository;

import BillBook_2025_backend.backend.entity.PointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {
    Optional<PointTransaction> findByImpUid(String impUid);
}
