package park.management.com.vn.constant;

/**
 * Lifecycle of a ticket/order.
 */
public enum TicketStatus {
    /** Ticket created but not yet paid */
    PENDING,

    /** Payment completed and verified */
    PAID,

    /** Ticket was cancelled before use */
    CANCELLED,

    /** Ticket was checked in or used */
    USED,

    /** Customer requested a refund */
    REFUND_REQUESTED,

    /** Refund was approved and processed */
    REFUNDED
}
