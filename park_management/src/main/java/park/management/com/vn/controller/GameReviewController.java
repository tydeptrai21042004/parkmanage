package park.management.com.vn.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import park.management.com.vn.model.request.GameReviewRequest;
import park.management.com.vn.model.response.GameReviewResponse;
import park.management.com.vn.security.UserPrincipal;
import park.management.com.vn.service.GameReviewService;

@RestController
@RequestMapping("/api/game-reviews")
@RequiredArgsConstructor
public class GameReviewController {

  private final GameReviewService service;

  // List approved reviews for a game
  @GetMapping("/by-game/{gameId}")
  public ResponseEntity<Page<GameReviewResponse>> listByGame(@PathVariable Long gameId, Pageable pageable) {
    return ResponseEntity.ok(service.listApprovedByGame(gameId, pageable));
  }

  // Create a review (must have REDEEMED pass for that game's ticket type)
  @PostMapping
  public ResponseEntity<GameReviewResponse> create(@AuthenticationPrincipal UserPrincipal user,
                                                   @RequestBody @Valid GameReviewRequest req) {
    return ResponseEntity.ok(service.create(user.getId(), req));
  }
}
