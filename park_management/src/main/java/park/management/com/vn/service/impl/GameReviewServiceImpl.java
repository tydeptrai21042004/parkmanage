package park.management.com.vn.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import park.management.com.vn.entity.Game;
import park.management.com.vn.entity.GameReview;
import park.management.com.vn.model.request.GameReviewRequest;
import park.management.com.vn.model.response.GameReviewResponse;
import park.management.com.vn.repository.GameRepository;
import park.management.com.vn.repository.GameReviewRepository;
import park.management.com.vn.repository.TicketPassRepository;
import park.management.com.vn.service.GameReviewService;

@Service
@RequiredArgsConstructor
public class GameReviewServiceImpl implements GameReviewService {

  private final GameRepository gameRepo;
  private final GameReviewRepository reviewRepo;
  private final TicketPassRepository passRepo;

  private static GameReviewResponse toDto(GameReview r) {
    return new GameReviewResponse(
        r.getId(),
        r.getGame().getId(),
        r.getUser().getId(),
        r.getRating(),
        r.getComment(),
        r.getApproved(),
        r.getCreatedAt()          // ✅ this is LocalDateTime from BaseEntity
    );
  }

  @Override
  @Transactional
  public GameReviewResponse create(Long userId, GameReviewRequest req) {
    Game g = gameRepo.findById(req.gameId()).orElseThrow(() -> new RuntimeException("GAME_NOT_FOUND"));

    long used = passRepo.countRedeemedByUserAndGame(userId, g.getId());
    if (used == 0) throw new RuntimeException("REVIEW_NOT_ALLOWED");

    GameReview r = new GameReview();
    r.setGame(g);
    var u = new park.management.com.vn.entity.UserEntity();
    u.setId(userId);
    r.setUser(u);
    r.setRating(req.rating());
    r.setComment(req.comment());
    r.setApproved(false);
    // ❌ Do not set createdAt here; BaseEntity/@CreationTimestamp handles it

    r = reviewRepo.save(r);
    return toDto(r);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<GameReviewResponse> listApprovedByGame(Long gameId, Pageable pageable) {
    return reviewRepo.findByGameIdAndApproved(gameId, true, pageable)
        .map(GameReviewServiceImpl::toDto);
  }

  @Override
  @Transactional
  public GameReviewResponse approve(Long reviewId, boolean approve, Long managerUserId) {
    // TODO: assert manager owns the game's branch if needed
    GameReview r = reviewRepo.findById(reviewId).orElseThrow(() -> new RuntimeException("REVIEW_NOT_FOUND"));
    r.setApproved(approve);
    r = reviewRepo.save(r);
    return toDto(r);
  }
}
