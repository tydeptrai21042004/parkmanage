package park.management.com.vn.exception.promotion;

import park.management.com.vn.exception.ErrorCode;

public class PromotionNotFoundException extends PromotionException {
    public PromotionNotFoundException(Long promotionId) {
        super(ErrorCode.PROMOTION_NOT_FOUND, "Promotion not found: id=" + promotionId);
    }
}
