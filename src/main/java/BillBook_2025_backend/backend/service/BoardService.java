// src/main/java/BillBook_2025_backend/backend/service/BoardService.java
package BillBook_2025_backend.backend.service;

import BillBook_2025_backend.backend.dto.BoardRequestDto;
import BillBook_2025_backend.backend.dto.BoardResponseDto;
import BillBook_2025_backend.backend.dto.CommentRequestDto;
import BillBook_2025_backend.backend.dto.CommentResponseDto;
import BillBook_2025_backend.backend.dto.LikeBoardResponseDto;
import BillBook_2025_backend.backend.dto.PictureDto;
import BillBook_2025_backend.backend.dto.PictureDtoList;
import BillBook_2025_backend.backend.entity.Board;
import BillBook_2025_backend.backend.entity.Book;
import BillBook_2025_backend.backend.entity.Comment;
import BillBook_2025_backend.backend.entity.LikeBook;
import BillBook_2025_backend.backend.entity.LikeBoard;
import BillBook_2025_backend.backend.entity.Member;
import BillBook_2025_backend.backend.entity.Picture;
import BillBook_2025_backend.backend.exception.BoardNotFoundException;
import BillBook_2025_backend.backend.exception.BookNotFoundException;
// import BillBook_2025_backend.backend.exception.ConflictException;
import BillBook_2025_backend.backend.exception.UnauthorizedException;

import BillBook_2025_backend.backend.repository.BoardRepository;
import BillBook_2025_backend.backend.repository.CommentRepository;
import BillBook_2025_backend.backend.repository.LikeBoardRepository;
import BillBook_2025_backend.backend.repository.MemberRepository;
import BillBook_2025_backend.backend.repository.PictureRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BoardService {
    private final BoardRepository boardRepo;
    private final CommentRepository commentRepo;
    private final LikeBoardRepository likeRepo;
    private final MemberRepository userRepository;
    private final S3UploadService s3UploadService;
    private final PictureRepository pictureRepository;

    public BoardService(BoardRepository boardRepo,
                        CommentRepository commentRepo,
                        LikeBoardRepository likeRepo,
                        MemberRepository userRepository,
                        S3UploadService s3UploadService,
                        PictureRepository pictureRepository) {
        this.boardRepo = boardRepo;
        this.commentRepo = commentRepo;
        this.likeRepo = likeRepo;
        this.userRepository = userRepository;
        this.s3UploadService = s3UploadService;
        this.pictureRepository = pictureRepository;
    }

    // 전체 게시글 조회
    public List<BoardResponseDto> findAll() {    
        return boardRepo.findAll().stream()
            .map(board -> {
                long likeCount = likeRepo.countByBoardId(board.getBoardId());

                // 일케 하면 모든 댓글을 다 불러와서 그 담에 사이즈 재는거라 느림
                // long commentsCount = commentRepo.findByBoard_BoardId(board.getBoardId()).size();
                long commentsCount = commentRepo.countByBoard_BoardId(board.getBoardId());
                
                return BoardResponseDto.fromEntity(board, likeCount, commentsCount);
            })
            .collect(Collectors.toList());
    }

    // 게시글 등록
    public BoardResponseDto create(BoardRequestDto dto, Long userId) {
        Member user = userRepository.findById(userId)
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
    public BoardResponseDto getById(Long boardId, Long userId) {
        Board board = boardRepo.findById(boardId)
            .orElseThrow(() -> new BoardNotFoundException("게시글이 존재하지 않습니다."));
        long likeCount = likeRepo.countByBoardId(boardId); 
        long commentsCount = commentRepo.countByBoard_BoardId(board.getBoardId());
        return BoardResponseDto.fromEntity(board, likeCount, commentsCount);
    }

    // 게시글 수정
    public BoardResponseDto update(Long boardId, BoardRequestDto dto, Long userId) {
        Member user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("존재하지 않는 사용자입니다."));

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

        long likeCount = likeRepo.countByBoardId(boardId);
        long commentsCount = commentRepo.countByBoard_BoardId(board.getBoardId());
        return BoardResponseDto.fromEntity(board, likeCount, commentsCount);
    }

    // 게시글 삭제
    public void delete(Long boardId, Long userId) {
        Member user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("존재하지 않는 사용자입니다."));

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
    
        return comments.stream()
            .map(CommentResponseDto::fromEntity)
            .collect(Collectors.toList());
    }

    // 댓글 등록
    public CommentResponseDto saveComment(Long boardId, CommentRequestDto dto, Long replyToId, Long userId) {
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

            if (Boolean.TRUE.equals(parentComment.getDeleted())) {
                throw new ResponseStatusException(HttpStatus.GONE, "부모 댓글이 이미 삭제되었습니다.");
            }

            comment.setReplyTo(parentComment);
        }
    
        Comment savedComment = commentRepo.save(comment);
    
        return CommentResponseDto.fromEntity(savedComment);
    }

    // 특정 댓글 삭제 (댓글 작성자만 가능)
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepo.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("접근 불가능한 댓글입니다"));
    
        if (Boolean.TRUE.equals(comment.getDeleted())) {
            throw new ResponseStatusException(HttpStatus.GONE, "이미 삭제된 댓글입니다.");
        }

        if (!comment.getUserId().equals(userId)) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }
    
        // commentRepo.delete(comment);
        comment.setDeleted(true);  // soft delete로 수정
        commentRepo.save(comment); // DB 반영
    }

    public Long like(Long boardId, Long userId) { //좋아요 누르기
        Member member = userRepository.findById(userId).orElseThrow(() -> new UnauthorizedException("로그인한 사용자만 이용이 가능합니다."));
        if (boardRepo.findById(boardId).isEmpty()) {
            throw new BoardNotFoundException("해당 게시글이 존재하지 않습니다.");
        } else {
            Optional<LikeBoard> existing = likeRepo.findByBoardBoardIdAndMemberId(boardId, userId);
            if (existing.isPresent()) { //좋아요 취소
                likeRepo.delete(existing.get());
            } else { //좋아요
                Board board = boardRepo.findById(boardId).get();
                LikeBoard likeBoard = new LikeBoard(board, member);
                likeRepo.save(likeBoard);
            }
            return likeRepo.countByBoardId(boardId);
        }
    }

    public Long checkLike(Long boardId) {
        if (boardRepo.findById(boardId).isEmpty()) {
            throw new BoardNotFoundException("해당 게시글이 존재하지 않습니다.");
        } else {
            return likeRepo.countByBoardId(boardId);
        }
    }

    public List<BoardResponseDto> searchBoards(String titleKeyword, String contentKeyword, String category) {
        // DB에서 조건에 맞는 게시글 조회
        List<Board> boards = boardRepo.findByTitleContainingOrContentContainingOrCategory(titleKeyword, contentKeyword, category);
    
        // Stream으로 돌면서 각 게시글에 대해 Board -> BoardResponseDto 변환
        return boards.stream()
                     .map(board -> {
                         long likeCount = likeRepo.countByBoardId(board.getBoardId());
                         long commentsCount = commentRepo.countByBoard_BoardId(board.getBoardId());
                         return BoardResponseDto.fromEntity(board, likeCount, commentsCount);
                     })
                     .collect(Collectors.toList());
    }
    
    @Transactional
    public PictureDtoList uploadImages(Long boardId, Long userId, List<MultipartFile> files) throws IOException {
        Member user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("존재하지 않는 사용자입니다."));

        Board board = boardRepo.findById(boardId)
            .orElseThrow(() -> new BoardNotFoundException("게시글이 존재하지 않습니다."));

        if (!board.getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        List<PictureDto> pictureDtos = new ArrayList<>();
        for (MultipartFile file : files) {
            PictureDto request = s3UploadService.saveFile(file);
            Picture picture = new Picture(request.getFilename(), request.getUrl(), board);
            board.getPicture().add(picture);
            pictureDtos.add(request);
        }
        PictureDtoList response = new PictureDtoList(pictureDtos);  //파일명도 줘야하나
        return response;
    }

    public void deleteImages(PictureDto request, Long boardId, Long userId) {
        Member user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("존재하지 않는 사용자입니다."));
        
        Board board = boardRepo.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("게시글이 존재하지 않습니다."));
                
        if (!board.getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        String filename = request.getFilename();
        Picture picture = pictureRepository.findByFilename(filename)
                .orElseThrow(() -> new EntityNotFoundException(filename + "가 존재하지 않습니다."));
        pictureRepository.delete(picture);
        s3UploadService.deleteImage(filename);
    }
}