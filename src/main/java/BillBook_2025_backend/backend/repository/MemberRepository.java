package BillBook_2025_backend.backend.repository;

import BillBook_2025_backend.backend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member save(Member member);
    Optional<Member> findById(Long id);
    Optional<Member> findByUserId(String userId);
    Optional<Member> findByEmail(String email);
    //User update(Long id, User user);
}
