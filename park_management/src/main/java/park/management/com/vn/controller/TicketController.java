package park.management.com.vn.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import park.management.com.vn.model.request.TicketRequest;
import park.management.com.vn.model.response.TicketResponse;
import park.management.com.vn.security.UserPrincipal;
import park.management.com.vn.service.TicketPassService;
import park.management.com.vn.service.TicketService;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

  private final TicketService ticketService;
  private final TicketPassService ticketPassService;

  @PostMapping
  public ResponseEntity<TicketResponse> create(@RequestBody @Valid TicketRequest ticketRequest,
                                               @AuthenticationPrincipal UserPrincipal user) {
    // user may be null for guest checkout
    Long userId = (user != null) ? user.getId() : null;

    // If guest (no userId), require customer info
    if (userId == null) {
      if (StringUtils.isBlank(ticketRequest.getCustomerName())
          || ticketRequest.getCustomerAge() == null
          || StringUtils.isBlank(ticketRequest.getCustomerEmail())
          || StringUtils.isBlank(ticketRequest.getCustomerPhone())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
      }
    }

    // Persist order and build response
    Long orderId = ticketService.createTicketOrder(ticketRequest, userId);
    TicketResponse resp = ticketService.getTicketResponseById(orderId);

    // Create passes for this order and attach
    resp.setPasses(ticketPassService.createForOrderId(orderId));

    return ResponseEntity.ok(resp);
  }
}
