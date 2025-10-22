package BillBook_2025_backend.backend.controller;

import BillBook_2025_backend.backend.dto.AuthRequest;
import BillBook_2025_backend.backend.dto.DeleteMemberDto;
import BillBook_2025_backend.backend.dto.MemberDto;
import BillBook_2025_backend.backend.dto.MemberResponseDto;
import BillBook_2025_backend.backend.entity.Member;
import BillBook_2025_backend.backend.service.EmailService;
import BillBook_2025_backend.backend.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {
    private final UserService userService;
    private final EmailService emailService;

    @Autowired
    public LoginController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<MemberResponseDto> login(@RequestBody MemberDto memberDto, HttpSession session) {
        MemberResponseDto userlogin = userService.login(memberDto);
        session.setAttribute("id", userlogin.getId());
        return ResponseEntity.ok(userlogin);
    }

    @DeleteMapping("/api/auth/login")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("로그아웃 성공");
    }

    @PostMapping("/api/auth/find/id")
    public ResponseEntity<String> findIdByEmail(@RequestBody AuthRequest request) {
        emailService.sendIdByEmail(request.getEmail());
        return ResponseEntity.ok("아이디가 이메일로 전송되었습니다.");
    }

    @PostMapping("/api/auth/find/password")
    public ResponseEntity<String> findPasswordByEmail(@RequestBody AuthRequest request) {
        emailService.sendPasswordByEmail(request.getUserId(), request.getEmail());
        return ResponseEntity.ok("비밀번호가 이메일로 전송되었습니다.");
    }

    @PostMapping("/api/auth/find/password/change")
    public ResponseEntity<String> changePasswordByEmail(@RequestBody AuthRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute("id");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        userService.changePassword(userId, request.getPassword(), request.getConfirmPassword());
        return ResponseEntity.ok("비밀번호가 변경되었습니다.");
    }

    @PostMapping("/api/auth/signup")
    @ResponseBody
    public ResponseEntity<String> signup(@RequestBody Member member) {
        userService.signup(member);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @DeleteMapping("/api/auth/signup")
    @ResponseBody
    public ResponseEntity<String> deleteUser(@RequestBody DeleteMemberDto request, HttpSession session) {
        Long userId = (Long) session.getAttribute("id");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        userService.delete(request, userId);
        return ResponseEntity.ok("회원탈퇴를 성공하였습니다.");
    }


}
