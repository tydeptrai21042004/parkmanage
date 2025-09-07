package park.management.com.vn.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import park.management.com.vn.entity.Game;
import park.management.com.vn.entity.ParkBranch;
import park.management.com.vn.model.request.GameRequest;
import park.management.com.vn.model.response.GameResponse;
import park.management.com.vn.repository.GameRepository;
import park.management.com.vn.repository.ParkBranchRepository;
import park.management.com.vn.service.GameService;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
  private final GameRepository gameRepo;
  private final ParkBranchRepository branchRepo;

  private static GameResponse toDto(Game g) {
    return new GameResponse(g.getId(), g.getParkBranch().getId(), g.getName(), g.getDescription(), g.getLocation());
    }

  @Override @Transactional
  public GameResponse create(GameRequest req) {
    ParkBranch b = branchRepo.findById(req.branchId()).orElseThrow(() -> new RuntimeException("BRANCH_NOT_FOUND"));
    Game g = new Game();
    g.setParkBranch(b);
    g.setName(req.name());
    g.setDescription(req.description());
    g.setLocation(req.location());
    return toDto(gameRepo.save(g));
  }

  @Override @Transactional
  public GameResponse update(Long id, GameRequest req) {
    Game g = gameRepo.findById(id).orElseThrow(() -> new RuntimeException("GAME_NOT_FOUND"));
    if (req.branchId() != null && !req.branchId().equals(g.getParkBranch().getId())) {
      var b = branchRepo.findById(req.branchId()).orElseThrow(() -> new RuntimeException("BRANCH_NOT_FOUND"));
      g.setParkBranch(b);
    }
    if (req.name() != null) g.setName(req.name());
    g.setDescription(req.description());
    g.setLocation(req.location());
    return toDto(gameRepo.save(g));
  }

  @Override @Transactional
  public void delete(Long id) { gameRepo.deleteById(id); }

  @Override @Transactional(readOnly = true)
  public GameResponse get(Long id) { return toDto(gameRepo.findById(id).orElseThrow(() -> new RuntimeException("GAME_NOT_FOUND"))); }

  @Override @Transactional(readOnly = true)
  public Page<GameResponse> list(Pageable pageable) {
    return gameRepo.findAll(pageable).map(GameServiceImpl::toDto);
  }
}
