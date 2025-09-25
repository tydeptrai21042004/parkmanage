package park.management.com.vn.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import park.management.com.vn.entity.OrderRefund;
import park.management.com.vn.model.request.RefundRequest;
import park.management.com.vn.service.RefundService;

@RestController
@RequestMapping("/api/orders/{orderId}")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    @PostMapping("/refund")
    public ResponseEntity<OrderRefund> create(@PathVariable Long orderId,
                                              @Valid @RequestBody RefundRequest request) {
        // service expects String reason, not a DTO
        OrderRefund refund = refundService.requestRefund(orderId, request.getReason());
        return ResponseEntity.ok(refund);
    }

    @GetMapping("/refund")
    public ResponseEntity<OrderRefund> get(@PathVariable Long orderId) {
        return ResponseEntity.ok(refundService.getRefund(orderId));
    }
}
