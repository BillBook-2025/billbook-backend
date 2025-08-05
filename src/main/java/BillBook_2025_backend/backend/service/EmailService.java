package BillBook_2025_backend.backend.service;

import BillBook_2025_backend.backend.entity.Member;
import BillBook_2025_backend.backend.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final MemberRepository memberRepository;
    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender, MemberRepository memberRepository) {
        this.mailSender = mailSender;
        this.memberRepository = memberRepository;
    }

    public void sendIdByEmail(String email) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다."));

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("junyeon03@gmail.com");
            message.setTo(email);
            message.setSubject("요청하신 아이디입니다.");
            message.setText("당신의 아이디는: " + member.getUserId());
            mailSender.send(message);

        } catch (MailException e) {
            // 로깅하거나 알림 보내기
            throw new RuntimeException("이메일 전송에 실패했습니다. 잠시 후 다시 시도해주세요.");
        }
    }

    public void sendPasswordByEmail(String userId, String email) {
        Member memberById = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 아이디입니다."));

        Member memberByEmail = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다."));

        if (!memberById.equals(memberByEmail)) {
            throw new IllegalArgumentException("입력된 정보가 일치하지 않습니다.");
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("junyeon03@gmail.com");
            message.setTo(email);
            message.setSubject("요청하신 비밀번호입니다.");
            message.setText("당신의 비밀번호는: " + memberByEmail.getPassword());
            mailSender.send(message);
        } catch (MailException e) {
            throw new RuntimeException("이메일 전송에 실패했습니다. 잠시 후 다시 시도해주세요.");
        }
    }
}
