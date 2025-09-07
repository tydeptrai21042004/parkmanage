package park.management.com.vn.exception.ticket;

import park.management.com.vn.exception.ErrorCode;

public class TicketTypeNotFoundException extends TicketException {
    public TicketTypeNotFoundException(Long ticketTypeId) {
        super(ErrorCode.TICKET_TYPE_NOT_FOUND, "Ticket type not found: id=" + ticketTypeId);
    }
}
