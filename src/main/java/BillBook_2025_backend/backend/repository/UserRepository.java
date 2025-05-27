package BillBook_2025_backend.backend.repository;

import BillBook_2025_backend.backend.entity.User;

import java.util.Optional;

public interface UserRepository {

    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByUserId(String userId);
    Optional<User> findByEmail(String email);
    User update(Long id, User user);
    void delete(Long id);
}
