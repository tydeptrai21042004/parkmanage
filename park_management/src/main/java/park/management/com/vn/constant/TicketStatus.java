package park.management.com.vn.constant;

public enum TicketStatus {
    PENDING,        // Ticket created but not yet paid
    PAID,           // Payment completed and verified
    CANCELLED,      // Ticket was cancelled before use
    USED,           // Ticket was checked in or used
    REFUND_REQUESTED, // Customer requested a refund
    REFUNDED         // Refund was approved and processed
}