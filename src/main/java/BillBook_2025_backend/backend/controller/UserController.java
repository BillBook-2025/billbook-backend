package BillBook_2025_backend.backend.controller;

import BillBook_2025_backend.backend.dto.BookListResponse;
import BillBook_2025_backend.backend.dto.UserInfoDto;
import BillBook_2025_backend.backend.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/api/{userId}/my")
    public ResponseEntity<UserInfoDto> getMyInfoDetails(HttpSession session,@PathVariable Long userId) {
        Long id = (Long) session.getAttribute("id");
        userService.checkPermission(id, userId);
        UserInfoDto response = userService.getMyInfoDetails(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/api/{userId}/my")
    public ResponseEntity<String> updateMyInfoDetails(HttpSession session, @RequestBody UserInfoDto request, @PathVariable Long userId) {
        Long id = (Long) session.getAttribute("id");
        userService.checkPermission(id, userId);
        userService.updateInfo(id, request);
        return ResponseEntity.ok("Success");
    }

    @GetMapping("/api/{userId}/my/like")
    public ResponseEntity<BookListResponse> getBookLikeList(HttpSession session, @PathVariable Long userId) {
        Long id = (Long) session.getAttribute("id");
        userService.checkPermission(id, userId);
        BookListResponse bookListResponse = userService.getBookLikeList(id);
        return ResponseEntity.ok(bookListResponse);

    }

    @GetMapping("/api/{userId}/my")
    public ResponseEntity<UserInfoDto> getPoints(HttpSession session, @PathVariable Long userId) {
        Long id = (Long) session.getAttribute("id");
        userService.checkPermission(id, userId);
        UserInfoDto points = userService.getPoints(userId);
        return ResponseEntity.ok(points);
    }

}
