package park.management.com.vn.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefundResponse {
    private final Long refundId;
    private final Long orderId;
    private final BigDecimal amount;
    private final String status;        // e.g. PENDING, SUCCESS, FAILED
    private final String reason;
    private final LocalDateTime createdAt;
}
