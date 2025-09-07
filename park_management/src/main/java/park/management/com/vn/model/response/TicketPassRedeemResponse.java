package park.management.com.vn.model.response;

import java.time.Instant;

public record TicketPassRedeemResponse(Long passId, String status, Instant redeemedAt) {}
