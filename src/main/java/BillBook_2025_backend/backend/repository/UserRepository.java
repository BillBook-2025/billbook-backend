package BillBook_2025_backend.backend.repository;

import BillBook_2025_backend.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByUserId(String userId);
    Optional<User> findByEmail(String email);
    //User update(Long id, User user);
}
