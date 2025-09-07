package park.management.com.vn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import park.management.com.vn.entity.Game;

public interface GameRepository extends JpaRepository<Game, Long> {}
