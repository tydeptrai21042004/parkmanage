package park.management.com.vn.exception.ticket;

import park.management.com.vn.exception.AppException;
import park.management.com.vn.exception.ErrorCode;

public class TicketException extends AppException {
    public TicketException(ErrorCode error) { super(error); }
    public TicketException(ErrorCode error, String message) { super(error, message); }
}
