package BillBook_2025_backend.backend.repository;

import BillBook_2025_backend.backend.entity.Follow;
import BillBook_2025_backend.backend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

<<<<<<< HEAD
import java.util.List;
=======
>>>>>>> 000uk_boards
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerAndFollowing(Member follower, Member following);
    List<Follow> findByFollower(Member follower);
    List<Follow> findByFollowing(Member following);
}
