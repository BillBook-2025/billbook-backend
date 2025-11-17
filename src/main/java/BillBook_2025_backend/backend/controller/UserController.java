package BillBook_2025_backend.backend.controller;

import BillBook_2025_backend.backend.dto.*;
import BillBook_2025_backend.backend.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/{userId}/my")
    public ResponseEntity<UserInfoDto> getMyInfoDetails(HttpSession session, @PathVariable Long userId) {
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

    @GetMapping("/api/{userId}/my/point")
    public ResponseEntity<UserInfoDto> getPoints(HttpSession session, @PathVariable Long userId) {
        Long id = (Long) session.getAttribute("id");
        userService.checkPermission(id, userId);
        UserInfoDto points = userService.getPoints(userId);
        return ResponseEntity.ok(points);
    }


    @GetMapping("/api/profile/{userId}")
    public ResponseEntity<ProfileDto> getProfileDetail(HttpSession session, @PathVariable Long userId) {
        Long id = (Long) session.getAttribute("id");
        userService.checkPermission(id, userId);
        ProfileDto profileDetail = userService.getProfileDetail(userId);
        return ResponseEntity.ok(profileDetail);
    }

    @GetMapping("/api/profile/{userId}/follower")
    public ResponseEntity<Map<String, List<FollowDto>>> getFollowers(HttpSession session, @PathVariable Long userId) {
        Long id = (Long) session.getAttribute("id");
        userService.checkPermission(id, userId);
        List<FollowDto> followers = userService.getFollowers(userId);
        return ResponseEntity.ok(Map.of("follower", followers));
    }

    @GetMapping("/api/profile/{userId}/following")
    public ResponseEntity<Map<String, List<FollowDto>>> getFollowings(HttpSession session, @PathVariable Long userId) {
        Long id = (Long) session.getAttribute("id");
        userService.checkPermission(id, userId);
        List<FollowDto> followings = userService.getFollowings(userId);
        return ResponseEntity.ok(Map.of("following", followings));
    }

    @PostMapping("/api/profile/{userId}/following")
    public ResponseEntity<String> follow(HttpSession session, @PathVariable Long userId, @RequestBody FollowDto request) {
        Long id = (Long) session.getAttribute("id");
        userService.checkPermission(id, userId);
        userService.addFollowing(userId, request.getUserId());
        return ResponseEntity.ok("ok");
    }

    @DeleteMapping("/api/profile/{userId}/following")
    public ResponseEntity<String> unfollow(HttpSession session, @PathVariable Long userId, @RequestBody FollowDto request) {
        Long id = (Long) session.getAttribute("id");
        userService.checkPermission(id, userId);
        userService.deleteFollowing(userId, request.getUserId());
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/api/profile/{userId}/history")
    public ResponseEntity<DealHistory> dealHistory(HttpSession session, @PathVariable Long userId, @RequestParam Long otherUserId) {
        Long id = (Long) session.getAttribute("id");
        userService.checkPermission(id, userId);
        DealHistory dealHistory = userService.getDealHistory(otherUserId, userId);
        return ResponseEntity.ok(dealHistory);
    }

    @PostMapping("/api/profile/{userId}/temperature")
    public ResponseEntity<String> reflectFeedback(HttpSession session, @PathVariable Long userId, @RequestBody String feedback) {
        Long id = (Long) session.getAttribute("id");
        userService.checkPermission(id, userId);
        userService.reflectFeedback(feedback, userId);
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/api/profile/{userId}/image")
    public ResponseEntity<PictureDto> uploadProfilePicture(HttpSession session, @PathVariable Long userId, @RequestPart MultipartFile file) throws IOException {
        Long id = (Long) session.getAttribute("id");
        userService.checkPermission(id, userId);
        PictureDto pictureDto = userService.uploadProfileImage(userId, file);
        return ResponseEntity.ok(pictureDto);
    }

    @GetMapping("/api/profile/{userId}/buy")
    public ResponseEntity<DataResponse> getBuyList(HttpSession session, @PathVariable Long userId) {
        DataResponse buyList = userService.getBuyList(userId);
        return ResponseEntity.ok(buyList);
    }

    @GetMapping("/api/profile/{userId}/sell")
    public ResponseEntity<DataResponse> getSellList(HttpSession session, @PathVariable Long userId) {
        DataResponse sellList = userService.getSellList(userId);
        return ResponseEntity.ok(sellList);
    }

    @GetMapping("/api/profile/{userId}/boards")
    public ResponseEntity<List<BoardResponseDto>> getBoardsList(HttpSession session, @PathVariable Long userId) {
        return ResponseEntity.ok(userService.getBoardsList(userId));
    }
}