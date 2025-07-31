package BillBook_2025_backend.backend.repository;

import BillBook_2025_backend.backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
