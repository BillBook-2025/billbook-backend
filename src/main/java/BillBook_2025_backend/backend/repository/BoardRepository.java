package BillBook_2025_backend.backend.repository;

import BillBook_2025_backend.backend.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
  /* 1. 특정 사용자 게시글만 조회: List<Board> findByWriterId(Long writerId);
   * 2. 제목이나 내용으로 검색: List<Board> findByTitleContaining(String keyword);
   * 3. 최신순 정렬된 게시글 가져오기: List<Board> findAllByOrderByCreatedAtDesc();
   * 결론: 게시판 기능(검색, 필터링, 정렬 등) 필요하다 → 위처럼 findBy... 메서드 추가  */
  List<Board> findByTitleContainingOrContentContainingOrCategory(String titleKeyword, String contentKeyword, String category);
  List<Board> findByUserId(long userId);
}