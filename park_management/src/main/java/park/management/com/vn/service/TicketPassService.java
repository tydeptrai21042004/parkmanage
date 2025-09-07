package park.management.com.vn.service;

import java.util.List;

import park.management.com.vn.entity.TicketOrder;
import park.management.com.vn.model.response.TicketPassLink;
import park.management.com.vn.model.response.TicketPassRedeemResponse;
import park.management.com.vn.model.response.TicketPassStatusResponse;

public interface TicketPassService {
  // Already had this
  List<TicketPassLink> createForOrder(TicketOrder order);

  // ✅ NEW: overload used by TicketController
  List<TicketPassLink> createForOrderId(Long orderId);

  TicketPassStatusResponse status(String code);
  TicketPassRedeemResponse redeem(String code, Long staffUserId);
}
