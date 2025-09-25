package park.management.com.vn.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import park.management.com.vn.constant.TicketStatus;

@Setter
@Getter
public class UpdateOrderStatusRequest {

  @NotNull
  private TicketStatus status;  // e.g. PENDING | PAID | CANCELED | REFUNDED

  private String note;          // optional remark (refund reason, etc.)
}
