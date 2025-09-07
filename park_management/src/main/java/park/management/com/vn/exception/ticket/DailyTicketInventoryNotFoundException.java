package park.management.com.vn.exception.ticket;

import java.time.LocalDate;
import park.management.com.vn.exception.ErrorCode;

public class DailyTicketInventoryNotFoundException extends TicketException {
    public DailyTicketInventoryNotFoundException(Long ticketTypeId, LocalDate date) {
        super(ErrorCode.TICKET_INVENTORY_NOT_FOUND,
              "No inventory found for ticketType=" + ticketTypeId + " at date " + date);
    }
}
