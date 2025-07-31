package BillBook_2025_backend.backend.repository;

import BillBook_2025_backend.backend.entity.Search;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchRepository extends JpaRepository<Search, Long> {
}
