package park.management.com.vn.model.response;

import java.time.LocalDate;

public record TicketPassStatusResponse(Long passId, String status, LocalDate ticketDate, Long branchId) {}
