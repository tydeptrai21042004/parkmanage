package park.management.com.vn.exception.promotion;

import park.management.com.vn.exception.ErrorCode;

public class PromotionNotActiveException extends PromotionException {
    public PromotionNotActiveException(Long promotionId) {
        super(ErrorCode.PROMOTION_NOT_ACTIVE, "Promotion not active: id=" + promotionId);
    }
}
