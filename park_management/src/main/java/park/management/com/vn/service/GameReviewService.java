package park.management.com.vn.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import park.management.com.vn.model.request.GameReviewRequest;
import park.management.com.vn.model.response.GameReviewResponse;

public interface GameReviewService {
  GameReviewResponse create(Long userId, GameReviewRequest req);
  Page<GameReviewResponse> listApprovedByGame(Long gameId, Pageable pageable);
  GameReviewResponse approve(Long reviewId, boolean approve, Long managerUserId);
}
