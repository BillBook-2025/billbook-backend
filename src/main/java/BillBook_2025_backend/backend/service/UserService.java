package BillBook_2025_backend.backend.service;

import BillBook_2025_backend.backend.entity.User;
import BillBook_2025_backend.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User signup(User user) {
        if (userRepository.findByUserId(user.getUserId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }

        User newUser = new User(user.getUserId(), user.getPassword(), user.getEmail(), user.getUserName());
        return userRepository.save(newUser);
    }

    public void delete(User user) {
        if (!userRepository.findByUserId(user.getUserId()).isPresent()) {
            throw new IllegalArgumentException("해당 유저가 존재하지 않습니다.");
        }

        userRepository.delete(user);
    }

    public User login(User user) {
        if(!userRepository.findByUserId(user.getUserId()).isPresent()) {
            throw new IllegalArgumentException("해당 아이디를 찾을 수 없습니다.");
        } else {
            User finduser = userRepository.findByUserId(user.getUserId()).get();
            if (!finduser.getPassword().equals(user.getPassword())) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }

            return finduser;
        }
    }

    @Transactional
    public void changePassword(String userId, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        } else {
            User user = userRepository.findByUserId(userId).get();
            user.setPassword(password);
        }

    }
}
