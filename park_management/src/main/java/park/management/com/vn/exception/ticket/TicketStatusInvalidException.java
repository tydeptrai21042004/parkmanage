package park.management.com.vn.exception.ticket;

import park.management.com.vn.exception.ErrorCode;

public class TicketStatusInvalidException extends TicketException {
    public TicketStatusInvalidException(String status) {
        super(ErrorCode.TICKET_STATUS_NOT_VALID, "Ticket status not valid: " + status);
    }
}
