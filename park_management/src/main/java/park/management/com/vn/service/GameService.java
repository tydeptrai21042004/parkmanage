package park.management.com.vn.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import park.management.com.vn.model.request.GameRequest;
import park.management.com.vn.model.response.GameResponse;

import java.util.List;

public interface GameService {
  GameResponse create(GameRequest req);
  GameResponse update(Long id, GameRequest req);
  void delete(Long id);
  GameResponse get(Long id);
  Page<GameResponse> list(Pageable pageable);

  // NEW: for /api/games/of-branch/{branchId}
  List<GameResponse> listOfBranch(Long branchId);

  // NEW: for /api/games/{id}/image (controller calls service.updateImage)
  void updateImage(Long id, String imageUrl);
}
