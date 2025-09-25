package park.management.com.vn.service;

import park.management.com.vn.constant.TicketStatus;
import park.management.com.vn.model.request.TicketRequest;
import park.management.com.vn.model.response.TicketResponse;

import java.util.List;

public interface OrderService {

  // guest allowed → userId may be null
  TicketResponse create(TicketRequest request, Long userId);

  TicketResponse get(Long id);

  List<TicketResponse> listByUser(Long userId);

  TicketResponse updateStatus(Long id, TicketStatus newStatus, String note);

  void cancelIfUnpaid(Long id);
}
