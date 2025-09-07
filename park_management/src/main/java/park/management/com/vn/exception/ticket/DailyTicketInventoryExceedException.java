package park.management.com.vn.exception.ticket;

import java.time.LocalDate;
import park.management.com.vn.exception.ErrorCode;

public class DailyTicketInventoryExceedException extends TicketException {
    public DailyTicketInventoryExceedException(LocalDate date) {
        super(ErrorCode.TICKET_INVENTORY_EXCEED,
              "Not enough inventory for date " + date);
    }
}
