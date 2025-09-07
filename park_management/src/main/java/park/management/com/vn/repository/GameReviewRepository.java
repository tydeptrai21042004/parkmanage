package park.management.com.vn.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import park.management.com.vn.entity.GameReview;

public interface GameReviewRepository extends JpaRepository<GameReview, Long> {
  Page<GameReview> findByGameIdAndApproved(Long gameId, Boolean approved, Pageable pageable);
}
