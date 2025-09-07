package park.management.com.vn.service;

import java.time.LocalDate;
import java.util.Optional;

import jakarta.validation.Valid;
import park.management.com.vn.entity.DailyTicketInventory;
import park.management.com.vn.entity.TicketOrder;
import park.management.com.vn.entity.TicketType;
import park.management.com.vn.model.request.TicketRequest;
import park.management.com.vn.model.response.TicketResponse;

public interface TicketService {

    TicketOrder getTicketOrderById(Long id);

    Optional<TicketOrder> findTicketOrderById(Long id);

    Optional<TicketType> findTicketTypeById(Long id);

    TicketType getTicketTypeById(Long id);

    DailyTicketInventory getDailyTicketInventory
            (Long ticketTypeId, LocalDate ticketDate);

    Long createTicketOrder(@Valid TicketRequest ticketRequest, Long userId);

    TicketResponse getTicketResponseById(Long ticketId);

    //TicketResponse getTicketResponseByID(Long id);

}
