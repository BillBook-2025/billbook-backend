package BillBook_2025_backend.backend.repository;

import BillBook_2025_backend.backend.entity.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MemoryUserRepository implements UserRepository{
    private final Map<Long, User> store = new HashMap<>();
    private Long nextId = 1L;

    public User save(User user) {
        user.setId(nextId++);
        store.put(user.getId(), user);
        return user;
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public Optional<User> findByUserId(String userId) {
        return store.values().stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst();
    }

    public Optional<User> findByEmail(String email) {
        return store.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }



    public User update(Long id, User user) {
        User oldUser = store.get(id);
        oldUser.setEmail(user.getEmail());
        oldUser.setUsername(user.getUsername());
        oldUser.setPassword(user.getPassword());
        oldUser.setTemperature(user.getTemperature());
        oldUser.setIsphoneverified(user.isIsphoneverified());

        return oldUser;
    }

    public void delete(Long id) {
        store.remove(id);
    }
}
