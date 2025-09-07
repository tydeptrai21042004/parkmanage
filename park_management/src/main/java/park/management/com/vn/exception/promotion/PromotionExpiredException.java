package park.management.com.vn.exception.promotion;

import park.management.com.vn.exception.ErrorCode;

public class PromotionExpiredException extends PromotionException {
    public PromotionExpiredException(Long promotionId) {
        super(ErrorCode.PROMOTION_EXPIRED, "Promotion expired: id=" + promotionId);
    }
}
