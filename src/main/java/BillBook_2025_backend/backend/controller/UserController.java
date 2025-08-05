package BillBook_2025_backend.backend.controller;

import BillBook_2025_backend.backend.dto.UserInfoDto;
import BillBook_2025_backend.backend.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/api/my")
    public ResponseEntity<UserInfoDto> getMyInfoDetails(HttpSession session){
        Long id = (Long) session.getAttribute("id");
        UserInfoDto response = userService.getMyInfoDetails(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/api/my")
    public ResponseEntity<String> updateMyInfoDetails(HttpSession session, @RequestBody UserInfoDto request) {
        Long id = (Long) session.getAttribute("id");
        userService.updateInfo(id, request);
        return ResponseEntity.ok("Success");
    }


}
