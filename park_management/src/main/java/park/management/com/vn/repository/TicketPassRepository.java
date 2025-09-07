package park.management.com.vn.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import park.management.com.vn.entity.TicketPass;

public interface TicketPassRepository extends JpaRepository<TicketPass, Long> {
  Optional<TicketPass> findByCode(String code);

  @Modifying
  @Query("""
    update TicketPass p
       set p.status = 'REDEEMED', p.redeemedAt = :now, p.redeemedByUserId = :userId
     where p.code = :code and p.status = 'ACTIVE'
  """)
  int redeemIfActive(@Param("code") String code, @Param("now") Instant now, @Param("userId") Long userId);

  // NEW: used to ensure a user actually used (REDEEMED) a ticket for a specific game
  @Query("""
    select count(p) from TicketPass p
      join p.order o
      join p.detail d
      join d.ticketType tt
      join tt.game g
     where o.userEntity.id = :userId
       and g.id = :gameId
       and p.status = 'REDEEMED'
  """)
  long countRedeemedByUserAndGame(@Param("userId") Long userId, @Param("gameId") Long gameId);
}
