package park.management.com.vn.service;

import park.management.com.vn.entity.OrderRefund;

public interface RefundService {
    OrderRefund requestRefund(Long orderId, String reason);
    OrderRefund getRefund(Long orderId); // <- make sure the name/signature matches your impl
}
