package BillBook_2025_backend.backend.service;

import BillBook_2025_backend.backend.entity.User;
import BillBook_2025_backend.backend.repository.UserRepository;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender, UserRepository userRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
    }

    public void sendIdByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다."));

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("junyeon03@gmail.com");
            message.setTo(email);
            message.setSubject("요청하신 아이디입니다.");
            message.setText("당신의 아이디는: " + user.getUserId());
            mailSender.send(message);

        } catch (MailException e) {
            // 로깅하거나 알림 보내기
            throw new RuntimeException("이메일 전송에 실패했습니다. 잠시 후 다시 시도해주세요.");
        }
    }

    public void sendPasswordByEmail(String userId, String email) {
        User userById = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 아이디입니다."));

        User userByEmail = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다."));

        if (!userById.equals(userByEmail)) {
            throw new IllegalArgumentException("입력된 정보가 일치하지 않습니다.");
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("junyeon03@gmail.com");
            message.setTo(email);
            message.setSubject("요청하신 비밀번호입니다.");
            message.setText("당신의 비밀번호는: " + userByEmail.getPassword());
            mailSender.send(message);
        } catch (MailException e) {
            throw new RuntimeException("이메일 전송에 실패했습니다. 잠시 후 다시 시도해주세요.");
        }
    }
}
