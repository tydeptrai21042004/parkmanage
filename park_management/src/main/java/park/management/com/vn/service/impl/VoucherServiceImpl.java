package park.management.com.vn.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import park.management.com.vn.entity.Voucher;
import park.management.com.vn.entity.VoucherUsage;
import park.management.com.vn.repository.VoucherRepository;
import park.management.com.vn.repository.VoucherUsageRepository;
import park.management.com.vn.service.VoucherService;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private static final ZoneId HCMC = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final BigDecimal MAX_LIMIT = new BigDecimal("500000");
    private static final BigDecimal HALF = new BigDecimal("0.5");

    private final VoucherRepository voucherRepo;
    private final VoucherUsageRepository usageRepo;

    @Override
    @Transactional
    public Result validateAndPrice(String code, Long userId, String guestEmail, BigDecimal subtotal) {
        if (code == null || code.isBlank()) return new Result(BigDecimal.ZERO, null);

        Voucher v = voucherRepo.findByCodeIgnoreCase(code.trim())
                .orElseThrow(() -> new IllegalArgumentException("VOUCHER_NOT_FOUND"));

        // active + within time window
        var now = LocalDateTime.now(HCMC);
        if (Boolean.FALSE.equals(v.getActive())) throw new IllegalStateException("VOUCHER_INACTIVE");
        if (now.isBefore(v.getStartAt()) || now.isAfter(v.getEndAt()))
            throw new IllegalStateException("VOUCHER_EXPIRED");

        // percent ≤ 0.5 & maxDiscount ≤ 500000 (defense-in-depth if DB constraints were bypassed)
        if (v.getPercent().compareTo(BigDecimal.ZERO) < 0 || v.getPercent().compareTo(HALF) > 0)
            throw new IllegalStateException("PERCENT_LIMIT_EXCEEDED");
        if (v.getMaxDiscount().compareTo(MAX_LIMIT) > 0)
            throw new IllegalStateException("MAX_DISCOUNT_LIMIT_EXCEEDED");

        // usage cap ≤ 3 per customer (by userId or guestEmail)
        long usedCount;
        if (userId != null) {
            usedCount = usageRepo.countByVoucher_CodeIgnoreCaseAndUserId(v.getCode(), userId);
        } else if (guestEmail != null && !guestEmail.isBlank()) {
            usedCount = usageRepo.countByVoucher_CodeIgnoreCaseAndGuestEmailIgnoreCase(v.getCode(), guestEmail.trim());
        } else {
            throw new IllegalStateException("IDENTITY_REQUIRED_FOR_VOUCHER");
        }
        if (usedCount >= 3) throw new IllegalStateException("VOUCHER_USAGE_LIMIT_REACHED");

        // compute discount = min(subtotal * percent, maxDiscount)
        BigDecimal byPercent = subtotal.multiply(v.getPercent());
        BigDecimal cap = v.getMaxDiscount().min(MAX_LIMIT); // extra guard
        BigDecimal discount = byPercent.min(cap);

        if (discount.signum() < 0) discount = BigDecimal.ZERO;
        return new Result(discount, v);
    }

    @Override
    @Transactional
    public void recordUsageIfNeeded(Long orderId, Long userId, String guestEmail, Voucher voucher) {
        if (voucher == null) return;
        if (usageRepo.existsByOrderId(orderId)) return;

        VoucherUsage u = new VoucherUsage();
        u.setVoucher(voucher);
        u.setUserId(userId);
        u.setGuestEmail(guestEmail);
        u.setOrderId(orderId);
        u.setUsedAt(LocalDateTime.now(HCMC));
        usageRepo.save(u);
    }
}
