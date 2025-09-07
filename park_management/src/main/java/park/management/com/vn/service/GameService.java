package park.management.com.vn.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import park.management.com.vn.model.request.GameRequest;
import park.management.com.vn.model.response.GameResponse;

public interface GameService {
  GameResponse create(GameRequest req);
  GameResponse update(Long id, GameRequest req);
  void delete(Long id);
  GameResponse get(Long id);
  Page<GameResponse> list(Pageable pageable);
}
