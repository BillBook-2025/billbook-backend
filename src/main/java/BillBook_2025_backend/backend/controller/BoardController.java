// ./gradlew bootRun 이거 터미널에서 실행
package BillBook_2025_backend.backend.controller;

import BillBook_2025_backend.backend.dto.*;
import BillBook_2025_backend.backend.entity.Comment;
import BillBook_2025_backend.backend.service.BoardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// @RestController
// @RequestMapping("/api/boards")
// public class BoardController {
//     private final BoardService boardService;
//     public BoardController(BoardService boardService) {
//         this.boardService = boardService;
//     }

//     @GetMapping("/test")
//     public String test() {
//         return "서버 정상 작동 중!";
//     }

//     @GetMapping // 전체 글 목록 불러오기
//     public ResponseEntity<List<BoardResponseDto>> getAllBoards(HttpSession session) {
//         // String userId = session.getAttribute("userId").toString();
//         return ResponseEntity.ok(boardService.findAll());
//     }

//     @PostMapping // 게시글 등록
//     public ResponseEntity<BoardResponseDto> postBoard(@RequestBody BoardRequestDto dto, HttpSession session) {
//         String userId = session.getAttribute("userId").toString();
//         return ResponseEntity.ok(boardService.create(dto, userId));
//     }

//     // 특정 게시글 조회
//     @GetMapping("/{board_id}")
//     public ResponseEntity<BoardResponseDto> getBoard(@PathVariable("board_id") Long boardId, HttpSession session) {
//         String userId = session.getAttribute("userId").toString();
//         return ResponseEntity.ok(boardService.getById(boardId, userId));
//     }

//     // 게시글 수정
//     @PatchMapping("/{board_id}")
//     public ResponseEntity<BoardResponseDto> updateBoard(@PathVariable("board_id") Long boardId, @RequestBody BoardRequestDto dto, HttpSession session) {
//     String userId = session.getAttribute("userId").toString();
//     return ResponseEntity.ok(boardService.update(boardId, dto, userId));
//     }

//     // 게시글 삭제
//     @DeleteMapping("/{board_id}")
//     public ResponseEntity<String> deletePost(@PathVariable("board_id") Long boardId, HttpSession session) {
//         String userId = session.getAttribute("userId").toString();
//         boardService.delete(boardId, userId);
//         return ResponseEntity.ok("게시물이 삭제되었습니다.");
//     }

//     // 특정 게시글의 댓글 조회
//     @GetMapping("/{board_id}/comments")
//     public ResponseEntity<List<CommentResponseDto>> getAllCommentsByBoardId(@PathVariable("board_id") Long boardId, HttpSession session) {
//         return ResponseEntity.ok(boardService.getCommentsByBoardId(boardId));
//     }

//     // 댓글 등록
//     @PostMapping("/{board_id}/comments")
//     public ResponseEntity<CommentResponseDto> postComment(@PathVariable("board_id") Long boardId, @RequestBody CommentRequestDto dto, HttpSession session) {
//         String userId = session.getAttribute("userId").toString();
//         return ResponseEntity.ok(boardService.saveComment(boardId, dto, null, userId));
//     }

//     // 대댓글 등록
//     @PostMapping("/{board_id}/comments/{comment_id}/replies")
//     public ResponseEntity<CommentResponseDto> postReply(@PathVariable("board_id") Long boardId, 
//                                                           @RequestBody CommentRequestDto dto,
//                                                           @PathVariable("comment_id") Long replyToId, 
//                                                           HttpSession session) {
//         String userId = session.getAttribute("userId").toString();
//         return ResponseEntity.ok(boardService.saveComment(boardId, dto, replyToId, userId));
//     }

//     // 댓글 삭제
//     @DeleteMapping("/{board_id}/comments/{comment_id}")
//     public ResponseEntity<String> deleteComment(@PathVariable("board_id") Long boardId,
//                                                 @PathVariable("comment_id") Long commentId,
//                                                 HttpSession session) {
//         String userId = session.getAttribute("userId").toString();
//         boardService.deleteComment(commentId, userId);
//         return ResponseEntity.ok("댓글이 삭제되었습니다.");
//     }
// }


@RestController
@RequestMapping("/api/boards")
public class BoardController {
    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/test")
    public String test() {
        return "서버 정상 작동 중!";
    }

    // 전체 글 목록
    @GetMapping
    public ResponseEntity<List<BoardResponseDto>> getAllBoards(HttpSession session) {
        return ResponseEntity.ok(boardService.findAll());
    }

    // 게시글 등록
    @PostMapping
    public ResponseEntity<BoardResponseDto> postBoard(@RequestBody BoardRequestDto dto, HttpSession session) {
        String userId = getLoginUserId(session);
        return ResponseEntity.ok(boardService.create(dto, userId));
    }

    // 특정 게시글 조회
    @GetMapping("/{board_id}")
    public ResponseEntity<BoardResponseDto> getBoard(@PathVariable("board_id") Long boardId, HttpSession session) {
        String userId = getLoginUserId(session);
        return ResponseEntity.ok(boardService.getById(boardId, userId));
    }

    // 게시글 수정
    @PatchMapping("/{board_id}")
    public ResponseEntity<BoardResponseDto> updateBoard(@PathVariable("board_id") Long boardId, 
                                                        @RequestBody BoardRequestDto dto, 
                                                        HttpSession session) {
        String userId = getLoginUserId(session);
        return ResponseEntity.ok(boardService.update(boardId, dto, userId));
    }

    // 게시글 삭제
    @DeleteMapping("/{board_id}")
    public ResponseEntity<String> deletePost(@PathVariable("board_id") Long boardId, HttpSession session) {
        String userId = getLoginUserId(session);
        boardService.delete(boardId, userId);
        return ResponseEntity.ok("게시물이 삭제되었습니다.");
    }

    // 특정 게시글의 댓글 조회
    @GetMapping("/{board_id}/comments")
    public ResponseEntity<List<CommentResponseDto>> getAllCommentsByBoardId(@PathVariable("board_id") Long boardId) {
        return ResponseEntity.ok(boardService.getCommentsByBoardId(boardId));
    }

    // 댓글 등록
    @PostMapping("/{board_id}/comments")
    public ResponseEntity<CommentResponseDto> postComment(@PathVariable("board_id") Long boardId, 
                                                          @RequestBody CommentRequestDto dto, 
                                                          HttpSession session) {
        String userId = getLoginUserId(session);
        return ResponseEntity.ok(boardService.saveComment(boardId, dto, null, userId));
    }

    // 대댓글 등록
    @PostMapping("/{board_id}/comments/{comment_id}/replies")
    public ResponseEntity<CommentResponseDto> postReply(@PathVariable("board_id") Long boardId,
                                                        @RequestBody CommentRequestDto dto,
                                                        @PathVariable("comment_id") Long replyToId,
                                                        HttpSession session) {
        String userId = getLoginUserId(session);
        return ResponseEntity.ok(boardService.saveComment(boardId, dto, replyToId, userId));
    }

    // 댓글 삭제
    @DeleteMapping("/{board_id}/comments/{comment_id}")
    public ResponseEntity<String> deleteComment(@PathVariable("board_id") Long boardId,
                                                @PathVariable("comment_id") Long commentId,
                                                HttpSession session) {
        String userId = getLoginUserId(session);
        boardService.deleteComment(commentId, userId);
        return ResponseEntity.ok("댓글이 삭제되었습니다.");
    }

    // ----------- 유틸 메서드 -----------
    private String getLoginUserId(HttpSession session) {
        Object userIdObj = session.getAttribute("id");
        if (userIdObj == null) {
            // 로그인 안 되어 있으면 401 Unauthorized 반환
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return userIdObj.toString();
    }
}