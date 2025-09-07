package park.management.com.vn.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import park.management.com.vn.model.request.TicketRequest;
import park.management.com.vn.model.response.TicketResponse;
import park.management.com.vn.security.UserPrincipal;
import park.management.com.vn.service.TicketService;

// NEW: add import for pass service + dto list
import park.management.com.vn.service.TicketPassService;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

  private final TicketService ticketService;

  // NEW: wire pass service
  private final TicketPassService ticketPassService;

  @PostMapping
  public ResponseEntity<TicketResponse> create(@RequestBody @Valid TicketRequest ticketRequest,
                                               @AuthenticationPrincipal UserPrincipal user) {
    Long userId = user.getId();

    // Persist order and get its id
    Long orderId = ticketService.createTicketOrder(ticketRequest, userId);

    // Build normal ticket response
    TicketResponse resp = ticketService.getTicketResponseById(orderId);

    // NEW: create per-ticket passes and attach to response
    // Implement ticketPassService.createForOrderId(orderId) as discussed.
    resp.setPasses(ticketPassService.createForOrderId(orderId));

    return ResponseEntity.ok(resp);
  }
}
