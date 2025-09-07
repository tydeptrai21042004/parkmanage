package park.management.com.vn.exception.ticket;

import park.management.com.vn.exception.ErrorCode;

public class TicketNotFoundException extends TicketException {
    public TicketNotFoundException(Long ticketOrderId) {
        super(ErrorCode.TICKET_NOT_FOUND, "Ticket order not found: id=" + ticketOrderId);
    }
}
