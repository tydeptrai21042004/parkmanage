package park.management.com.vn.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    PRICE_NOT_FOUND("PRICE_NOT_FOUND", "Price not found", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND("CUSTOMER_NOT_FOUND", "User not found", HttpStatus.NOT_FOUND),
    PARK_BRANCH_NOT_FOUND("PARK_BRANCH_NOT_FOUND", "Park branch not found", HttpStatus.NOT_FOUND),
    TICKET_NOT_FOUND("TICKET_NOT_FOUND", "Ticket not found", HttpStatus.NOT_FOUND),
    PROMOTION_NOT_FOUND("PROMOTION_NOT_FOUND", "Promotion not found", HttpStatus.NOT_FOUND),
    TICKET_STATUS_NOT_VALID("TICKET_STATUS_NOT_VALID", "Ticket status not valid", HttpStatus.BAD_REQUEST),
    USER_PASSWORD_INVALID("USER_PASSWORD_INVALID", "User password invalid", HttpStatus.BAD_REQUEST),
    TICKET_TYPE_NOT_FOUND("TICKET_TYPE_NOT_FOUND", "Ticket type not found", HttpStatus.NOT_FOUND),
    PROMOTION_NOT_ACTIVE("PROMOTION_NOT_ACTIVE", "Promotion is not active", HttpStatus.BAD_REQUEST),
    PROMOTION_EXPIRED("PROMOTION_EXPIRED", "Promotion is expired", HttpStatus.BAD_REQUEST),
    TICKET_INVENTORY_NOT_FOUND("TICKET_INVENTORY_NOT_FOUND", "Ticket inventory not found", HttpStatus.BAD_REQUEST),
    TICKET_INVENTORY_EXCEED("TICKET_INVENTORY_EXCEED", "Ticket inventory exceeded", HttpStatus.BAD_REQUEST),
    PROMOTION_INVALID_BRANCH("PROMOTION_INVALID_BRANCH", "Promotion does not belong to this branch", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public HttpStatus getStatus() { return status; }
}
