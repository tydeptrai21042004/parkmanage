package park.management.com.vn.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import park.management.com.vn.entity.DailyTicketInventory;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailyTicketInventoryRepository extends JpaRepository<DailyTicketInventory, Long> {

    // Lock the (ticketType, date) row so concurrent purchases can’t oversell
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<DailyTicketInventory> getDailyTicketInventoriesByTicketType_IdAndDate(
            Long ticketTypeId, LocalDate ticketDate);
}
