package park.management.com.vn.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import park.management.com.vn.entity.TicketPass;

@Repository
public interface TicketPassRepository extends JpaRepository<TicketPass, Long> {

  Optional<TicketPass> findByCode(String code);

  @Modifying
  @Query("""
    update TicketPass p
       set p.status = 'REDEEMED',
           p.redeemedAt = :now,
           p.redeemedByUserId = :userId
     where p.code = :code
       and p.status = 'ACTIVE'
  """)
  int redeemIfActive(@Param("code") String code,
                     @Param("now") Instant now,
                     @Param("userId") Long userId);

  // check if any pass is already redeemed (checked-in)
  boolean existsByOrder_IdAndRedeemedAtIsNotNull(Long orderId);

  // list passes for an order
  List<TicketPass> findByOrder_Id(Long orderId);

  /**
   * Count redeemed passes for a user for the game’s branch.
   * (Game -> ParkBranch; compare against order.parkBranch.id)
   */
  @Query("""
    select count(p) from TicketPass p
      join p.order o
     where o.userEntity.id = :userId
       and o.parkBranch.id = (
            select g.parkBranch.id from Game g where g.id = :gameId
       )
       and p.status = 'REDEEMED'
  """)
  long countRedeemedByUserAndGame(@Param("userId") Long userId,
                                  @Param("gameId") Long gameId);
}
