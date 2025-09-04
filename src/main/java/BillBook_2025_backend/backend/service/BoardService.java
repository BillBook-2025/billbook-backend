// src/main/java/BillBook_2025_backend/backend/service/BoardService.java
package BillBook_2025_backend.backend.service;

import BillBook_2025_backend.backend.dto.BoardRequestDto;
import BillBook_2025_backend.backend.dto.BoardResponseDto;
import BillBook_2025_backend.backend.dto.CommentRequestDto;
import BillBook_2025_backend.backend.dto.CommentResponseDto;
// import BillBook_2025_backend.backend.dto.LikeStatusDto;
import BillBook_2025_backend.backend.entity.Board;
import BillBook_2025_backend.backend.entity.Comment;
// import BillBook_2025_backend.backend.entity.LikeBoard;
import BillBook_2025_backend.backend.entity.Member;

import BillBook_2025_backend.backend.exception.BoardNotFoundException;
// import BillBook_2025_backend.backend.exception.ConflictException;
import BillBook_2025_backend.backend.exception.UnauthorizedException;

import BillBook_2025_backend.backend.repository.BoardRepository;
import BillBook_2025_backend.backend.repository.CommentRepository;
// import BillBook_2025_backend.backend.repository.MemoryLikeBoardRepository;
import BillBook_2025_backend.backend.repository.MemberRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BoardService {
    private final BoardRepository boardRepo;
    private final CommentRepository commentRepo;
    // private final MemoryLikeBoardRepository likeRepo;
    private final MemberRepository userRepository;

    public BoardService(BoardRepository boardRepo,
                        CommentRepository commentRepo,
                        // MemoryLikeBoardRepository likeRepo,
                        MemberRepository userRepository) {
        this.boardRepo = boardRepo;
        this.commentRepo = commentRepo;
        // this.likeRepo = likeRepo;
        this.userRepository = userRepository;
    }

    // 전체 게시글 조회
    public List<BoardResponseDto> findAll() {    
        return boardRepo.findAll().stream()
            .map(board -> {
                long likeCount = 0; //likeRepo.countByBoardId(board.getBoardId());
                long commentsCount = commentRepo.findByBoard_BoardId(board.getBoardId()).size();
                return BoardResponseDto.fromEntity(board, likeCount, commentsCount);
            })
            .collect(Collectors.toList());
    }

    // 게시글 등록
    public BoardResponseDto create(BoardRequestDto dto, String userId) {
        Long id = Long.valueOf(userId); // userId를 Long으로 변환
        Member user = userRepository.findById(id)
                .orElseThrow(() -> new UnauthorizedException("존재하지 않는 사용자입니다."));
        // Member user = userRepository.findByUserId(userId)
        //     .orElseThrow(() -> new UnauthorizedException("존재하지 않는 사용자입니다."));

        Board board = new Board();
        board.setTitle(dto.getTitle());
        board.setCategory(dto.getCategory());
        board.setIsbn(dto.getIsbn());
        board.setContent(dto.getContent());
        board.setUserId(user.getUserId());
        board.setCreatedAt(LocalDateTime.now());

        boardRepo.save(board);
        return BoardResponseDto.fromEntity(board, 0, 0);
    }

    // 게시글 상세 조회
    public BoardResponseDto getById(Long boardId, String userId) {
        Board board = boardRepo.findById(boardId)
            .orElseThrow(() -> new BoardNotFoundException("게시글이 존재하지 않습니다."));

        long likeCount = 0; //likeRepo.countByBoardId(boardId); 
        long commentsCount = commentRepo.findByBoard_BoardId(board.getBoardId()).size();
        return BoardResponseDto.fromEntity(board, likeCount, commentsCount);
    }

    // 게시글 수정
    public BoardResponseDto update(Long boardId, BoardRequestDto dto, String userId) {
        Long id = Long.valueOf(userId); // userId를 Long으로 변환
        Member user = userRepository.findById(id)
                .orElseThrow(() -> new UnauthorizedException("존재하지 않는 사용자입니다."));
        // Member user = userRepository.findByUserId(userId)
        //     .orElseThrow(() -> new UnauthorizedException("존재하지 않는 사용자입니다."));

        Board board = boardRepo.findById(boardId)
            .orElseThrow(() -> new BoardNotFoundException("게시글이 존재하지 않습니다."));

        if (!board.getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        board.setTitle(dto.getTitle());
        board.setCategory(dto.getCategory());
        board.setIsbn(dto.getIsbn());
        board.setContent(dto.getContent());
        boardRepo.save(board);

        long likeCount = 0; //likeRepo.countByBoardId(boardId);
        long commentsCount = commentRepo.findByBoard_BoardId(board.getBoardId()).size();
        return BoardResponseDto.fromEntity(board, likeCount, commentsCount);
    }

    // 게시글 삭제
    public void delete(Long boardId, String userId) {
        Long id = Long.valueOf(userId); // userId를 Long으로 변환
        Member user = userRepository.findById(id)
                .orElseThrow(() -> new UnauthorizedException("존재하지 않는 사용자입니다."));
        // Member user = userRepository.findByUserId(userId)
        //     .orElseThrow(() -> new UnauthorizedException("존재하지 않는 사용자입니다."));

        Board board = boardRepo.findById(boardId)
            .orElseThrow(() -> new BoardNotFoundException("게시글이 존재하지 않습니다."));

        if (!board.getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        boardRepo.delete(board);
    }

    // 특정 게시글의 댓글 조회
    public List<CommentResponseDto> getCommentsByBoardId(Long boardId) {
        List<Comment> comments = commentRepo.findByBoard_BoardId(boardId);

        // // 만약에 부모 댓글이 삭제되면 어케 처리할까
        // for(int i = 0; i < comments.size(); i++) {
        //     if(commentRepo.findById(comments.get(i).getReplyTo().getCommentId()) == null) {
        //         // 흠..
        //     }
        // }

        return comments.stream()
            .map(CommentResponseDto::fromEntity)
            .collect(Collectors.toList());
    }

    // 댓글 등록
    public CommentResponseDto saveComment(Long boardId, CommentRequestDto dto, Long replyToId, String userId) {
        Board board = boardRepo.findById(boardId)
            .orElseThrow(() -> new BoardNotFoundException("게시글이 존재하지 않습니다."));
    
        Comment comment = new Comment();
        comment.setBoard(board);
        comment.setContent(dto.getContent());
        comment.setUserId(userId);
        comment.setCreatedAt(LocalDateTime.now());

        if (replyToId != null) {
            Comment parentComment = commentRepo.findById(replyToId)
                .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다."));
            comment.setReplyTo(parentComment);
        }
    
        Comment savedComment = commentRepo.save(comment);
    
        return CommentResponseDto.fromEntity(savedComment);
    }

    // 특정 댓글 삭제 (댓글 작성자만 가능)
    public void deleteComment(Long commentId, String userId) {
        Comment comment = commentRepo.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("접근 불가능한 댓글입니다"));
    
        if (!comment.getUserId().equals(userId)) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }
    
        commentRepo.delete(comment);
    }
}
