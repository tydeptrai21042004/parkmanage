package park.management.com.vn.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import park.management.com.vn.constant.TicketStatus;
import park.management.com.vn.model.request.TicketRequest;
import park.management.com.vn.model.request.UpdateOrderStatusRequest;
import park.management.com.vn.model.response.TicketResponse;
import park.management.com.vn.security.UserPrincipal;
import park.management.com.vn.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "order-controller")
public class OrderController {

  private final OrderService orderService;

  private Long currentUserIdOrNull() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null) return null;
    Object p = auth.getPrincipal();
    if (p instanceof UserPrincipal up) return up.getId();
    return null;
  }

  // POST /api/orders (guest allowed)
  @PostMapping
  public ResponseEntity<TicketResponse> create(@Valid @RequestBody TicketRequest request) {
    Long userId = currentUserIdOrNull(); // null for guest
    return ResponseEntity.ok(orderService.create(request, userId));
  }

  // GET /api/orders/{id}
  @GetMapping("/{id}")
  public ResponseEntity<TicketResponse> get(@PathVariable Long id) {
    return ResponseEntity.ok(orderService.get(id));
  }

  // GET /api/orders?userId=...
  @GetMapping
  public ResponseEntity<List<TicketResponse>> listByUser(@RequestParam Long userId) {
    return ResponseEntity.ok(orderService.listByUser(userId));
  }

  // PUT /api/orders/{id} (paid | canceled | refunded)
  @PutMapping("/{id}")
  public ResponseEntity<TicketResponse> updateStatus(@PathVariable Long id,
                                                     @Valid @RequestBody UpdateOrderStatusRequest request) {
    TicketStatus newStatus = request.getStatus();
    String note = request.getNote();
    return ResponseEntity.ok(orderService.updateStatus(id, newStatus, note));
  }

  // DELETE /api/orders/{id} (cancel if unpaid per policy)
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    orderService.cancelIfUnpaid(id);
    return ResponseEntity.noContent().build();
  }
}
