package park.management.com.vn.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import park.management.com.vn.model.response.*;
import park.management.com.vn.security.UserPrincipal;
import park.management.com.vn.service.TicketPassService;

@RestController
@RequestMapping("/api/passes")
@RequiredArgsConstructor
public class TicketPassController {
  private final TicketPassService svc;

  @GetMapping("/{code}")
  public ResponseEntity<TicketPassStatusResponse> status(@PathVariable String code) {
    return ResponseEntity.ok(svc.status(code));
  }

  @PostMapping("/{code}/redeem")
  @PreAuthorize("hasAuthority('TICKET_VALIDATE')")
  public ResponseEntity<TicketPassRedeemResponse> redeem(@PathVariable String code, Authentication auth) {
    Long staffId = ((UserPrincipal) auth.getPrincipal()).getId();
    return ResponseEntity.ok(svc.redeem(code, staffId));
  }
}
