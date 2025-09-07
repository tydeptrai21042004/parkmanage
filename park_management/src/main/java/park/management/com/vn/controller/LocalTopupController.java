package park.management.com.vn.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import park.management.com.vn.model.request.ConfirmTopupRequest;
import park.management.com.vn.service.WalletTopupService;

@RestController
@RequestMapping("/api/local/topups")
@RequiredArgsConstructor
public class LocalTopupController {
  private final WalletTopupService topupService;

  @PostMapping("/confirm")
  public ResponseEntity<Void> confirm(@Valid @RequestBody ConfirmTopupRequest req) {
    topupService.confirmTopup(req.getOrderCode(), req.getAmount(), req.getPaymentLinkId());
    return ResponseEntity.ok().build();
  }
}
